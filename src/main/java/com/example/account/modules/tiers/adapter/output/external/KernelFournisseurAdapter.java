package com.example.account.modules.tiers.adapter.output.external;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelThirdPartyResponse;
import com.example.account.modules.tiers.domain.model.Fournisseur;
import com.example.account.modules.tiers.domain.model.enums.TypeClient;
import com.example.account.modules.tiers.domain.port.output.ActorContactServicePort;
import com.example.account.modules.tiers.domain.port.output.FournisseurRepositoryPort;
import com.example.account.modules.tiers.domain.port.output.ThirdPartySaleConfigServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class KernelFournisseurAdapter implements FournisseurRepositoryPort {

    private final WebClient salesCoreWebClient;
    private final ThirdPartySaleConfigServicePort saleConfigServicePort;
    private final ActorContactServicePort actorContactServicePort;

    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelThirdPartyResponse>>> THIRD_PARTY_LIST_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<KernelThirdPartyResponse>> THIRD_PARTY_TYPE =
            new ParameterizedTypeReference<>() {};

    public KernelFournisseurAdapter(@Qualifier("salesCoreWebClient") WebClient salesCoreWebClient,
                                     ThirdPartySaleConfigServicePort saleConfigServicePort,
                                     ActorContactServicePort actorContactServicePort) {
        this.salesCoreWebClient = salesCoreWebClient;
        this.saleConfigServicePort = saleConfigServicePort;
        this.actorContactServicePort = actorContactServicePort;
    }

    /** See KernelClientAdapter.withSaleConfig — same sale-config sub-resource, same reasoning. */
    private Mono<Fournisseur> withSaleConfig(Fournisseur fournisseur) {
        return saleConfigServicePort.getConfig(fournisseur.getIdFournisseur())
                .doOnNext(config -> {
                    fournisseur.setAllowedSaleSizes(config.getAllowedSaleSizes());
                    fournisseur.setNTva(config.isVatApplicable());
                })
                .thenReturn(fournisseur)
                .onErrorReturn(fournisseur);
    }

    /** See KernelClientAdapter.withContactEmail — same actor address-book sub-resource, same reasoning. */
    private Mono<Fournisseur> withContactEmail(Fournisseur fournisseur, UUID actorId) {
        if (actorId == null) {
            return Mono.just(fournisseur);
        }
        return actorContactServicePort.getPrimaryEmail(actorId)
                .doOnNext(email -> {
                    if (!email.isBlank()) {
                        fournisseur.setEmail(email);
                    }
                })
                .thenReturn(fournisseur)
                .onErrorReturn(fournisseur);
    }

    private Mono<UUID> getOrganizationId() {
        return Mono.deferContextual(ctx -> {
            UUID orgId = ctx.getOrDefault(ReactiveOrganizationContext.ORGANIZATION_ID_KEY, null);
            if (orgId == null) {
                return Mono.error(new IllegalStateException("Organization ID absent du contexte réactif"));
            }
            return Mono.just(orgId);
        });
    }

    @Override
    public Mono<Fournisseur> findById(UUID id) {
        // /api/fournisseurs/{id} crashes server-side (ClassCastException in
        // Kernel's accounting-core, per the sales-core merge notes) — the
        // generic third-party endpoint returns the exact same record shape
        // without going through that broken path.
        return salesCoreWebClient
                .get()
                .uri("/api/third-parties/{id}", id)
                .retrieve()
                .bodyToMono(THIRD_PARTY_TYPE)
                .map(KernelApiResponse::getData)
                .flatMap(c -> withContactEmail(mapToFournisseur(c), c.getPartyId()))
                .flatMap(this::withSaleConfig);
    }

    @Override
    public Mono<Fournisseur> findByUsername(String username) {
        return findAllActiveFournisseurs()
                .filter(f -> username.equalsIgnoreCase(f.getUsername()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé avec username: " + username)));
    }

    @Override
    public Mono<Fournisseur> findByEmail(String email) {
        return findAllActiveFournisseurs()
                .filter(f -> email.equalsIgnoreCase(f.getEmail()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé avec email: " + email)));
    }

    @Override
    public Mono<Fournisseur> findByCodeFournisseur(String codeFournisseur) {
        return findAllActiveFournisseurs()
                .filter(f -> codeFournisseur.equalsIgnoreCase(f.getCodeFournisseur()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Fournisseur non trouvé avec code: " + codeFournisseur)));
    }

    @Override
    public Flux<Fournisseur> findByTypeFournisseur(TypeClient typeFournisseur) {
        return findAllActiveFournisseurs().filter(f -> typeFournisseur == f.getTypeFournisseur());
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return findByUsername(username).map(f -> true).onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return findByEmail(email).map(f -> true).onErrorReturn(false);
    }

    // See KernelClientAdapter.ENRICHMENT_CONCURRENCY — same reasoning: unbounded
    // per-item flatMap here burst 15-20+ simultaneous new connections to
    // kernel-core, which resets some of them under that burst.
    private static final int ENRICHMENT_CONCURRENCY = 3;

    @Override
    public Flux<Fournisseur> findAllActiveFournisseurs() {
        // Same as findById: /api/fournisseurs crashes server-side, so this
        // goes through the generic third-party listing filtered by role instead.
        return getOrganizationId().flatMapMany(orgId ->
                salesCoreWebClient
                        .get()
                        .uri(uriBuilder -> uriBuilder.path("/api/third-parties")
                                .queryParam("organizationId", orgId)
                                .queryParam("role", "SUPPLIER")
                                .build())
                        .header("X-Organization-Id", orgId.toString())
                        .retrieve()
                        .bodyToMono(THIRD_PARTY_LIST_TYPE)
                        .map(KernelApiResponse::getData)
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(f -> withContactEmail(mapToFournisseur(f, orgId), f.getPartyId()), ENRICHMENT_CONCURRENCY)
                        .flatMap(this::withSaleConfig, ENRICHMENT_CONCURRENCY)
        );
    }

    @Override
    public Mono<Long> countActiveFournisseurs() {
        return findAllActiveFournisseurs().count();
    }

    @Override
    public Mono<Fournisseur> save(Fournisseur fournisseur) {
        return Mono.error(new UnsupportedOperationException("La création/modification de fournisseurs est gérée par le Kernel"));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return Mono.error(new UnsupportedOperationException("La suppression de fournisseurs est gérée par le Kernel"));
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return findById(id).map(f -> true).onErrorReturn(false);
    }

    @Override
    public Mono<Void> resendCredentials(UUID id, String email, String name) {
        return getOrganizationId().flatMap(orgId ->
                salesCoreWebClient
                        .post()
                        .uri("/api/fournisseurs/{id}/invite", id)
                        .bodyValue(Map.of(
                                "clientId", id.toString(),
                                "organizationId", orgId.toString(),
                                "email", email,
                                "name", name
                        ))
                        .retrieve()
                        .bodyToMono(Void.class)
        );
    }

    @Override
    public Mono<Void> ensurePortalAccess(UUID id, String email, String name) {
        return getOrganizationId().flatMap(orgId ->
                salesCoreWebClient
                        .post()
                        .uri("/api/fournisseurs/{id}/ensure-portal-access", id)
                        .bodyValue(Map.of(
                                "clientId", id.toString(),
                                "organizationId", orgId.toString(),
                                "email", email,
                                "name", name
                        ))
                        .retrieve()
                        .bodyToMono(Boolean.class)
        ).then();
    }

    private Fournisseur mapToFournisseur(KernelThirdPartyResponse c) {
        return mapToFournisseur(c, null);
    }

    /**
     * Same caveat as KernelClientAdapter: Kernel's third-party record has no
     * contact details or commercial terms beyond what's mapped here — those
     * fields aren't just unmapped, they don't exist on this record at all.
     */
    private Fournisseur mapToFournisseur(KernelThirdPartyResponse c, UUID organizationId) {
        Fournisseur f = new Fournisseur();
        f.setIdFournisseur(c.getId());
        f.setOrganizationId(organizationId);
        f.setUsername(c.getDisplayName() != null ? c.getDisplayName() : c.getName());
        f.setRaisonSociale(c.getLongName() != null ? c.getLongName() : c.getName());
        f.setCodeFournisseur(c.getCode() != null ? c.getCode() : c.getReferenceCode());
        f.setNumeroTva(c.getTaxNumber());
        f.setNTva(c.getVatSubject() != null ? c.getVatSubject() : false);
        f.setLimiteCredit(c.getAuthorizedCreditLimit());
        f.setActif(c.getActive() != null ? c.getActive() : true);
        f.setTypeFournisseur(c.getLegalForm() != null ? TypeClient.ENTREPRISE : TypeClient.PARTICULIER);
        return f;
    }
}
