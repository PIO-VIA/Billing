package com.example.account.modules.tiers.adapter.output.external;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelThirdPartyResponse;
import com.example.account.modules.tiers.domain.model.Client;
import com.example.account.modules.tiers.domain.model.enums.TypeClient;
import com.example.account.modules.tiers.domain.port.output.ActorContactServicePort;
import com.example.account.modules.tiers.domain.port.output.ClientRepositoryPort;
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
public class KernelClientAdapter implements ClientRepositoryPort {

    private final WebClient salesCoreWebClient;
    private final ThirdPartySaleConfigServicePort saleConfigServicePort;
    private final ActorContactServicePort actorContactServicePort;

    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelThirdPartyResponse>>> CLIENT_LIST_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<KernelThirdPartyResponse>> CLIENT_TYPE =
            new ParameterizedTypeReference<>() {};

    public KernelClientAdapter(@Qualifier("salesCoreWebClient") WebClient salesCoreWebClient,
                               ThirdPartySaleConfigServicePort saleConfigServicePort,
                               ActorContactServicePort actorContactServicePort) {
        this.salesCoreWebClient = salesCoreWebClient;
        this.saleConfigServicePort = saleConfigServicePort;
        this.actorContactServicePort = actorContactServicePort;
    }

    /**
     * Kernel's third-party record has no concept of allowed sale sizes or VAT
     * applicability — those live on a separate per-third-party sale-config
     * sub-resource (GET /api/third-parties/{id}/sale-config). Missing config
     * (never set for this third party yet) just leaves the client at its
     * defaults rather than failing the whole lookup.
     */
    private Mono<Client> withSaleConfig(Client client) {
        return saleConfigServicePort.getConfig(client.getIdClient())
                .doOnNext(config -> {
                    client.setAllowedSaleSizes(config.getAllowedSaleSizes());
                    client.setNTva(config.isVatApplicable());
                })
                .thenReturn(client)
                .onErrorReturn(client);
    }

    /**
     * Same idea as withSaleConfig, but for email: Kernel's third-party record
     * has none, so this looks it up from the underlying actor's own address
     * book (see ActorContactServicePort) using partyId — the actor id, not
     * the third-party record's own id. Left blank (not an error) if the
     * actor has no contact on file yet.
     */
    private Mono<Client> withContactEmail(Client client, UUID actorId) {
        if (actorId == null) {
            return Mono.just(client);
        }
        return actorContactServicePort.getPrimaryEmail(actorId)
                .doOnNext(email -> {
                    if (!email.isBlank()) {
                        client.setEmail(email);
                    }
                })
                .thenReturn(client)
                .onErrorReturn(client);
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
    public Mono<Client> findById(UUID id) {
        return salesCoreWebClient
                .get()
                .uri("/api/customers/{id}", id)
                .retrieve()
                .bodyToMono(CLIENT_TYPE)
                .map(KernelApiResponse::getData)
                .flatMap(c -> withContactEmail(mapToClient(c), c.getPartyId()))
                .flatMap(this::withSaleConfig);
    }

    @Override
    public Mono<Client> findByUsername(String username) {
        return findAllActiveClients()
                .filter(c -> username.equalsIgnoreCase(c.getUsername()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé avec username: " + username)));
    }

    @Override
    public Mono<Client> findByEmail(String email) {
        return findAllActiveClients()
                .filter(c -> email.equalsIgnoreCase(c.getEmail()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé avec email: " + email)));
    }

    @Override
    public Mono<Client> findByCodeClient(String codeClient) {
        return findAllActiveClients()
                .filter(c -> codeClient.equalsIgnoreCase(c.getCodeClient()))
                .next()
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Client non trouvé avec code: " + codeClient)));
    }

    @Override
    public Flux<Client> findByTypeClient(TypeClient typeClient) {
        return findAllActiveClients().filter(c -> typeClient == c.getTypeClient());
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return findByUsername(username).map(c -> true).onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return findByEmail(email).map(c -> true).onErrorReturn(false);
    }

    // Each client here needs two extra Kernel round-trips (contact email,
    // sale-config) — flatMap's default concurrency (256) fired every one of
    // those at once for the whole list, bursting 15-20+ brand-new HTTPS
    // connections to kernel-core simultaneously. Kernel-core resets some of
    // them under that burst ("connection observed an error" / "Connection
    // reset by peer" in the logs), which is what made the client/fournisseur
    // list intermittently hang or come back empty. Capping concurrency
    // trades a bit of latency for not tripping whatever's rejecting bursts
    // on Kernel's side.
    private static final int ENRICHMENT_CONCURRENCY = 3;

    @Override
    public Flux<Client> findAllActiveClients() {
        return getOrganizationId().flatMapMany(orgId ->
                salesCoreWebClient
                        .get()
                        .uri(uriBuilder -> uriBuilder.path("/api/customers").queryParam("organizationId", orgId).build())
                        .header("X-Organization-Id", orgId.toString())
                        .retrieve()
                        .bodyToMono(CLIENT_LIST_TYPE)
                        .map(KernelApiResponse::getData)
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(c -> withContactEmail(mapToClient(c, orgId), c.getPartyId()), ENRICHMENT_CONCURRENCY)
                        .flatMap(this::withSaleConfig, ENRICHMENT_CONCURRENCY)
        );
    }

    @Override
    public Mono<Long> countActiveClients() {
        return findAllActiveClients().count();
    }

    @Override
    public Mono<Client> save(Client client) {
        return Mono.error(new UnsupportedOperationException("La création/modification de clients est gérée par le Kernel"));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return Mono.error(new UnsupportedOperationException("La suppression de clients est gérée par le Kernel"));
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return findById(id).map(c -> true).onErrorReturn(false);
    }

    @Override
    public Mono<Long> count() {
        return findAllActiveClients().count();
    }

    @Override
    public Mono<Void> resendCredentials(UUID id, String email, String name) {
        return getOrganizationId().flatMap(orgId ->
                salesCoreWebClient
                        .post()
                        .uri("/api/customers/{id}/invite", id)
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
                        .uri("/api/customers/{id}/ensure-portal-access", id)
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

    private Client mapToClient(KernelThirdPartyResponse c) {
        return mapToClient(c, null);
    }

    /**
     * Kernel's third-party record has no concept of contact details (address/
     * phone/email/website) or commercial terms (credit limit, running balance,
     * VAT registration, allowed sale sizes) — those fields simply aren't
     * present here and are left at their defaults, not just unmapped. Only
     * legalForm hints at company-vs-individual, so that's the best signal
     * available for typeClient; ADMINISTRATION has no equivalent at all here.
     */
    private Client mapToClient(KernelThirdPartyResponse c, UUID organizationId) {
        Client client = new Client();
        client.setIdClient(c.getId());
        client.setOrganizationId(organizationId);
        client.setUsername(c.getDisplayName() != null ? c.getDisplayName() : c.getName());
        client.setRaisonSociale(c.getLongName() != null ? c.getLongName() : c.getName());
        client.setCodeClient(c.getCode() != null ? c.getCode() : c.getReferenceCode());
        client.setNumeroTva(c.getTaxNumber());
        client.setNTva(c.getVatSubject() != null ? c.getVatSubject() : false);
        client.setTypeClient(c.getLegalForm() != null ? TypeClient.ENTREPRISE : TypeClient.PARTICULIER);
        client.setLimiteCredit(c.getAuthorizedCreditLimit());
        client.setActif(c.getActive() != null ? c.getActive() : true);
        return client;
    }
}
