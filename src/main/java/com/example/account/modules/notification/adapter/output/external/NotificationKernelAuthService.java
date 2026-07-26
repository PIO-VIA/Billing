package com.example.account.modules.notification.adapter.output.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Same cached-token pattern as AccountingKernelAuthService, but logged in under the
 * "notification" Kernel client application instead of the default
 * "comops.kernel.client-id" one: the default client isn't entitled to the NOTIFICATION
 * platform service (see ClientApplicationServiceEntitlementWebFilter on the Kernel side),
 * so a token minted under that client would 401 as soon as it's paired with the
 * notification client's X-Client-Id/X-Api-Key headers on the actual send call.
 */
@Service
@Slf4j
public class NotificationKernelAuthService {

    private final WebClient authWebClient;
    private final String username;
    private final String password;
    private final Mono<AuthData> cachedAuth;

    public NotificationKernelAuthService(
            WebClient.Builder builder,
            @Value("${comops.kernel.base-url}") String baseUrl,
            @Value("${comops.kernel.notification.client-id}") String clientId,
            @Value("${comops.kernel.notification.api-key}") String apiKey,
            @Value("${comops.kernel.tenant-id}") String tenantId,
            @Value("${comops.kernel.service-account.username}") String username,
            @Value("${comops.kernel.service-account.password}") String password) {
        this.authWebClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Client-Id", clientId)
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("X-Tenant-Id", tenantId)
                .build();
        this.username = username;
        this.password = password;
        this.cachedAuth = Mono.defer(this::login)
                .cache(this::ttlFor, error -> Duration.ZERO, () -> Duration.ZERO);
    }

    public Mono<String> getValidToken() {
        return cachedAuth.map(AuthData::getAccessToken);
    }

    private Mono<AuthData> login() {
        log.info("Logging in to Kernel as notification service account '{}'", username);
        return authWebClient.post()
                .uri("/api/auth/login")
                .bodyValue(Map.of("principal", username, "password", password))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AuthData>>() {})
                .map(ApiResponse::getData)
                .doOnNext(auth -> log.info("Kernel notification service-account login succeeded, token expires in {}s",
                        auth.getExpiresInSeconds()))
                .timeout(Duration.ofSeconds(15))
                .doOnError(err -> log.warn("Kernel notification service-account login failed: {}", err.getMessage()));
    }

    private Duration ttlFor(AuthData auth) {
        long expiresIn = auth.getExpiresInSeconds() != null ? auth.getExpiresInSeconds() : 60;
        return Duration.ofSeconds(Math.max(expiresIn - 60, 30));
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApiResponse<T> {
        private Boolean success;
        private T data;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AuthData {
        private String accessToken;
        private Long expiresInSeconds;
    }
}
