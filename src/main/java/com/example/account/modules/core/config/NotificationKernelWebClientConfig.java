package com.example.account.modules.core.config;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.notification.adapter.output.external.NotificationKernelAuthService;
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
    public WebClient notificationKernelWebClient(WebClient.Builder builder, NotificationKernelAuthService authService) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Client-Id", clientId)
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("X-Tenant-Id", tenantId)
                .filter(injectBearerToken(authService))
                .filter(injectOrganizationId())
                .build();
    }

    private ExchangeFilterFunction injectBearerToken(NotificationKernelAuthService authService) {
        return (request, next) -> {
            if (request.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                return next.exchange(request);
            }
            return authService.getValidToken()
                    .flatMap(token -> next.exchange(ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .build()));
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
