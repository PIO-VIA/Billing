package com.example.account.modules.core.config;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.util.JwtClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
@Slf4j
public class KernelWebClientConfig {

    @Value("${comops.kernel.base-url}")
    private String baseUrl;

    @Value("${comops.kernel.client-id}")
    private String clientId;

    @Value("${comops.kernel.api-key}")
    private String apiKey;

    @Value("${comops.kernel.tenant-id}")
    private String tenantId;

    @Bean
    @Qualifier("kernelWebClient")
    public WebClient kernelWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Client-Id", clientId)
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("X-Tenant-Id", tenantId)
                .filter(injectBearerToken())
                .filter(injectOrganizationId())
                .filter(injectTenantId())
                .build();
    }

    /**
     * No service-account fallback: every call either already has an explicit
     * Authorization header set by its caller, or forwards whatever token the
     * frontend's own request carried (via OrganizationFilter →
     * ReactiveOrganizationContext.TOKEN_KEY) — same as salesCoreWebClient.
     * A call with no caller token at all (and no explicit one set) simply
     * goes out bare and Kernel rejects it on its own terms, rather than this
     * silently substituting a different identity's permissions.
     */
    private static final java.util.Set<String> UNAUTHENTICATED_AUTH_PATHS = java.util.Set.of(
            "/api/auth/login", "/api/auth/discover-contexts", "/api/auth/select-context", "/api/auth/login/mfa/confirm",
            "/api/auth/refresh");

    private ExchangeFilterFunction injectBearerToken() {
        return (request, next) -> {
            if (request.headers().containsKey(HttpHeaders.AUTHORIZATION)
                    || UNAUTHENTICATED_AUTH_PATHS.contains(request.url().getPath())) {
                return next.exchange(request);
            }
            return Mono.deferContextual(ctx -> {
                String token = ctx.getOrDefault(ReactiveOrganizationContext.TOKEN_KEY, null);
                if (token == null) {
                    return next.exchange(request);
                }
                return next.exchange(ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build());
            });
        };
    }

    /**
     * X-Tenant-Id defaults to the platform tenant, but Kernel tokens are
     * minted for a specific tenant (the "tid" claim) and Kernel silently
     * returns empty results — not an error — when the header doesn't match
     * the tenant the token/org actually belongs to. Overrides the default
     * with whatever tenant the request's own bearer token (explicit or
     * context-injected, whichever ends up on the request) was issued for.
     */
    private ExchangeFilterFunction injectTenantId() {
        return (request, next) -> {
            String authHeader = request.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
            String tenantId = JwtClaims.readClaim(token, "tid");
            if (tenantId == null || tenantId.isBlank()) {
                return next.exchange(request);
            }
            ClientRequest modified = ClientRequest.from(request)
                    .headers(h -> h.set("X-Tenant-Id", tenantId))
                    .build();
            return next.exchange(modified);
        };
    }

    /**
     * Reads the organization ID from the Reactor context and adds X-Organization-Id to all Kernel requests.
     */
    private ExchangeFilterFunction injectOrganizationId() {
        return (request, next) -> Mono.deferContextual(ctx -> {
            UUID orgId = ctx.getOrDefault(ReactiveOrganizationContext.ORGANIZATION_ID_KEY, null);
            if (orgId != null) {
                ClientRequest modified = ClientRequest.from(request)
                        .header("X-Organization-Id", orgId.toString())
                        .build();
                return next.exchange(modified);
            }
            return next.exchange(request);
        });
    }
}
