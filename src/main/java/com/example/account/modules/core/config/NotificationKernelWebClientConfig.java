package com.example.account.modules.core.config;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.util.JwtClaims;
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
 * Mirrors kernelWebClient (same base URL, same org-id-from-context filter) but
 * authenticates under the "notification" Kernel client application — the default
 * client (comops.kernel.client-id) isn't entitled to the NOTIFICATION platform
 * service, so it can't be reused for /api/notifications/**.
 */
@Configuration
public class NotificationKernelWebClientConfig {

    @Value("${comops.kernel.base-url}")
    private String baseUrl;

    @Value("${comops.kernel.notification.client-id}")
    private String clientId;

    @Value("${comops.kernel.notification.api-key}")
    private String apiKey;

    @Value("${comops.kernel.tenant-id}")
    private String tenantId;

    @Bean
    @Qualifier("notificationKernelWebClient")
    public WebClient notificationKernelWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Client-Id", clientId)
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("X-Tenant-Id", tenantId)
                .filter(injectBearerToken())
                .filter(injectTenantId())
                .filter(injectOrganizationId())
                .build();
    }

    /**
     * Same override as kernelWebClient: the default X-Tenant-Id above is the
     * platform tenant, but a forwarded caller token is minted for that caller's
     * real tenant — sending the platform tenant alongside it is a mismatch
     * Kernel rejects outright with 401, even though the token itself is valid.
     */
    private ExchangeFilterFunction injectTenantId() {
        return (request, next) -> {
            String authHeader = request.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
            String tid = JwtClaims.readClaim(token, "tid");
            if (tid == null || tid.isBlank()) {
                return next.exchange(request);
            }
            ClientRequest modified = ClientRequest.from(request)
                    .headers(h -> h.set("X-Tenant-Id", tid))
                    .build();
            return next.exchange(modified);
        };
    }

    /**
     * Forwards the caller's own token (same as kernelWebClient/salesCoreWebClient)
     * whenever one is available in Reactor context — every notification send is
     * triggered by a real HTTP request with a real caller token already in
     * context. No service-account fallback: a send with no caller token in
     * context (e.g. a future background/scheduled trigger with nobody's request
     * to borrow from) errors out here rather than silently using a different
     * identity's permissions — every call site already wraps sends in
     * onErrorResume, so this degrades to a logged, swallowed failure same as
     * any other notification-send error.
     */
    private ExchangeFilterFunction injectBearerToken() {
        return (request, next) -> {
            if (request.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                return next.exchange(request);
            }
            return Mono.deferContextual(ctx -> {
                String token = ctx.getOrDefault(ReactiveOrganizationContext.TOKEN_KEY, null);
                if (token == null) {
                    return Mono.error(new IllegalStateException(
                            "No caller token available to send this notification"));
                }
                return next.exchange(ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build());
            });
        };
    }

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
