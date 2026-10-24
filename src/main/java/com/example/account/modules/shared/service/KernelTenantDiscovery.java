package com.example.account.modules.shared.service;

import com.example.account.modules.core.exception.SalesCoreErrorMapper;
import com.example.account.modules.shared.dto.kernel.KernelApiResponse;
import com.example.account.modules.shared.dto.kernel.KernelDiscoverContextsResponse;
import com.example.account.modules.shared.dto.kernel.KernelDiscoveredContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Kernel's /api/auth/login needs the right X-Tenant-Id up front — it has no
 * account-wide fallback, so logging in against the wrong tenant (e.g. a
 * WebClient's platform-default header) just looks like invalid credentials,
 * even for a real account whose tenant simply isn't the platform one.
 * discover-contexts has no such requirement (it searches across every
 * tenant), so this is the shared first step for every Kernel login path —
 * staff Sign In/Try Out, portal customer/supplier login, and the
 * accounting service-account login — rather than three copies of the same
 * discovery call.
 */
@Component
public class KernelTenantDiscovery {

    private static final ParameterizedTypeReference<KernelApiResponse<KernelDiscoverContextsResponse>> KERNEL_DISCOVER_TYPE =
            new ParameterizedTypeReference<>() {};

    public Mono<List<KernelDiscoveredContext>> discoverContexts(WebClient kernelWebClient, String principal, String password) {
        return kernelWebClient
                .post()
                .uri("/api/auth/discover-contexts")
                .bodyValue(Map.of("principal", principal, "password", password))
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        resp -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")))
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class).defaultIfEmpty("")
                                .flatMap(err -> Mono.error(new ResponseStatusException(
                                        HttpStatus.BAD_GATEWAY, "Kernel discover-contexts error: " + SalesCoreErrorMapper.extractMessage(err)))))
                .bodyToMono(KERNEL_DISCOVER_TYPE)
                .map(KernelApiResponse::getData)
                .timeout(Duration.ofSeconds(15))
                .map(discovered -> discovered.getContexts() == null ? List.<KernelDiscoveredContext>of() : discovered.getContexts());
    }

    /**
     * Picks the first context with at least one organization (a bare
     * PLATFORM/no-org context is never where a real tenant relationship
     * lives), with no picker for the rare case of an account spanning more
     * than one org-bearing tenant — callers that need to offer the user a
     * choice (see AuthServiceAdapter's Sign In/Try Out flow) use
     * discoverContexts directly instead of this shortcut.
     */
    public Mono<UUID> resolveSingleTenantId(WebClient kernelWebClient, String principal, String password) {
        return discoverContexts(kernelWebClient, principal, password)
                .flatMap(contexts -> {
                    UUID tenantId = contexts.stream()
                            .filter(c -> c.getOrganizations() != null && !c.getOrganizations().isEmpty())
                            .findFirst()
                            .map(KernelDiscoveredContext::getTenantId)
                            .orElse(null);
                    return tenantId == null
                            ? Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No Kernel tenant found for this account."))
                            : Mono.just(tenantId);
                });
    }
}
