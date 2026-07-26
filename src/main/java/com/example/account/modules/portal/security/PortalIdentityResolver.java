package com.example.account.modules.portal.security;

import com.example.account.modules.shared.dto.kernel.KernelActorResponse;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelOrganizationResponse;
import com.example.account.modules.shared.dto.kernel.KernelThirdPartyResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared "which organizations does this actor do business in" resolution,
 * used by both PortalAuthController (login) and PortalDataController
 * (per-request re-verification). Kernel has no cross-org third-party search,
 * so this brute-forces it: GET /api/organizations (the only Kernel listing
 * that isn't membership-scoped) then GET /api/third-parties?organizationId=X
 * per org, keeping the ones with a matching partyId. With 40+ orgs in the
 * tenant this genuinely takes 10-15s — the portal dashboard fires four of
 * these near-simultaneously (quotations/invoices/purchase-orders/supplier-
 * invoices), so without caching it redoes the full scan four times over.
 * A short-lived per-actor cache means concurrent calls share one scan and
 * a page's worth of navigation doesn't re-pay the cost each time. Entries
 * are never evicted (only their value expires and gets refetched on next
 * use) — fine at this account scale, would need a real eviction policy at
 * production scale.
 */
@Component
public class PortalIdentityResolver {

    private final WebClient kernelWebClient;
    private final Map<UUID, Mono<List<PortalOrgAssociation>>> associationsCache = new ConcurrentHashMap<>();
    private static final Duration CACHE_TTL = Duration.ofSeconds(90);

    private static final ParameterizedTypeReference<KernelApiResponse<KernelActorResponse>> USER_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelThirdPartyResponse>>> THIRD_PARTY_LIST_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<KernelApiResponse<List<KernelOrganizationResponse>>> ORG_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    public PortalIdentityResolver(@Qualifier("kernelWebClient") WebClient kernelWebClient) {
        this.kernelWebClient = kernelWebClient;
    }

    /**
     * Not /api/actors/me: that requires a "business actor profile" (full
     * onboarding), which most client/fournisseur accounts never have — it
     * 404s with BUSINESS_ACTOR_NOT_FOUND for them. /api/users/me works for
     * any UserAccount and returns the same actorId we need.
     */
    public Mono<KernelActorResponse> resolveActor(String bearerToken) {
        return kernelWebClient
                .get()
                .uri("/api/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .retrieve()
                .bodyToMono(USER_TYPE)
                .map(KernelApiResponse::getData);
    }

    public Mono<List<PortalOrgAssociation>> resolveAssociations(UUID actorId) {
        if (actorId == null) {
            return Mono.just(List.of());
        }
        return associationsCache.computeIfAbsent(actorId, id -> scanAllOrganizations(id).cache(CACHE_TTL));
    }

    // Same reasoning as KernelClientAdapter.ENRICHMENT_CONCURRENCY: unbounded
    // flatMap here fires one new connection per organization in the tenant
    // (40+) at once, which is exactly the kind of burst that trips Kernel
    // into resetting some of them.
    private static final int SCAN_CONCURRENCY = 3;

    private Mono<List<PortalOrgAssociation>> scanAllOrganizations(UUID actorId) {
        return listAllOrganizations()
                .flatMap(org -> findThirdParty(actorId, org)
                        .map(tp -> new PortalOrgAssociation(
                                org.getId(),
                                org.getDisplayName() != null ? org.getDisplayName() : org.getShortName(),
                                tp.getId(),
                                tp.getDisplayName() != null ? tp.getDisplayName() : tp.getName(),
                                tp.getRoles())), SCAN_CONCURRENCY)
                .collectList();
    }

    private Flux<KernelOrganizationResponse> listAllOrganizations() {
        return kernelWebClient
                .get()
                .uri("/api/organizations")
                .retrieve()
                .bodyToMono(ORG_LIST_TYPE)
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable);
    }

    /** Empty (not an error) when the org isn't subscribed to the service, or the actor just has no record there — both mean "skip this org". */
    private Mono<KernelThirdPartyResponse> findThirdParty(UUID actorId, KernelOrganizationResponse org) {
        return kernelWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/api/third-parties").queryParam("organizationId", org.getId()).build())
                .retrieve()
                .bodyToMono(THIRD_PARTY_LIST_TYPE)
                .map(KernelApiResponse::getData)
                .flatMapMany(Flux::fromIterable)
                .filter(tp -> actorId.equals(tp.getPartyId()))
                .next()
                .onErrorResume(e -> Mono.empty());
    }
}
