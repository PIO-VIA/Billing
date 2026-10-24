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

/**
 * Products/customers/fournisseurs/sellers/auth now live in sales-core, not
 * Kernel directly (sales-core itself talks to Kernel and adds its own
 * augmentation on top, e.g. allowedSaleSizes/ntva for third parties). Forwards
 * the caller's own Authorization + X-Organization-Id, same as kernelWebClient —
 * sales-core's Kernel leg is hardwired to a service account internally, but
 * org-scoping on sales-core's own endpoints still depends on these headers.
 */
@Configuration
@Slf4j
public class SalesCoreWebClientConfig {

    @Value("${sales-core.base-url}")
    private String baseUrl;

    @Value("${comops.kernel.client-id}")
    private String clientId;

    @Value("${comops.kernel.api-key}")
    private String apiKey;

    @Value("${comops.kernel.tenant-id}")
    private String tenantId;

    @Bean
    @Qualifier("salesCoreWebClient")
    public WebClient salesCoreWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                // sales-core now lives inside kernel-core itself, so the same
                // X-Client-Id/X-Api-Key/X-Tenant-Id kernel-core's edge auth filter
                // requires on every /api/** call apply here too — sales-core's own
                // routes happen to be exempted from it, but product-core/tp-core
                // routes (products, fournisseurs, customers) are not.
                .defaultHeader("X-Client-Id", clientId)
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("X-Tenant-Id", tenantId)
                .filter(injectBearerToken())
                .filter(injectOrganizationId())
                .filter(injectTenantId())
                .filter(injectAgencyId())
                .build();
    }

    private ExchangeFilterFunction injectBearerToken() {
        return (request, next) -> Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault(ReactiveOrganizationContext.TOKEN_KEY, null);
            if (token != null && !request.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                ClientRequest modified = ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build();
                return next.exchange(modified);
            }
            return next.exchange(request);
        });
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
     * Kernel has no X-Organization-Id equivalent that works for staff whose
     * permissions are granted at a specific agency rather than org/tenant-wide
     * (e.g. "inventory:read#AGENCY:<uuid>") — those calls 403 with "droits
     * insuffisants" unless X-Agency-Id names the agency the grant is for.
     * JwtClaims.readSingleAgencyId only returns a value when the token's
     * permissions agree on exactly one agency, so this never overrides an
     * already-correct org/tenant-wide request and never guesses among
     * multiple agencies on a multi-agency token.
     */
    private ExchangeFilterFunction injectAgencyId() {
        return (request, next) -> {
            if (request.headers().containsKey("X-Agency-Id")) {
                return next.exchange(request);
            }
            String authHeader = request.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
            String agencyId = JwtClaims.readSingleAgencyId(token);
            if (agencyId == null || agencyId.isBlank()) {
                return next.exchange(request);
            }
            ClientRequest modified = ClientRequest.from(request)
                    .header("X-Agency-Id", agencyId)
                    .build();
            return next.exchange(modified);
        };
    }

    private ExchangeFilterFunction injectOrganizationId() {
        return (request, next) -> {
            if (request.headers().containsKey("X-Organization-Id")) {
                return next.exchange(request);
            }
            return Mono.deferContextual(ctx -> {
                UUID orgId = ctx.getOrDefault(ReactiveOrganizationContext.ORGANIZATION_ID_KEY, null);
                if (orgId != null) {
                    ClientRequest modified = ClientRequest.from(request)
                            .header("X-Organization-Id", orgId.toString())
                            .build();
                    return next.exchange(modified);
                }
                return next.exchange(request);
            });
        };
    }
}
