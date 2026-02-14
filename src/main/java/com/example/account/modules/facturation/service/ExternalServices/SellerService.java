package com.example.account.modules.facturation.service.ExternalServices;

import com.example.account.modules.facturation.dto.response.ExternalResponses.SellerAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {

    private final WebClient.Builder webClientBuilder;

    @Value("${comops.core.backend_ip}")
    private String comOpsUrl;

    public Flux<SellerAuthResponse> getSellersByOrganization(UUID organizationId) {
        
        String url = String.format(
                "http://%s/sellers/organization/%s/sellers",
                comOpsUrl,
                organizationId
        );

        log.info("Requesting sellers from: {}", url);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    log.error("Error calling external Seller service: {}", response.statusCode());
                    return Mono.empty();
                })
                .bodyToFlux(SellerAuthResponse.class)
                .doOnComplete(() -> log.info("Successfully requested sellers."))
                .onErrorResume(e -> {
                    log.error("Error calling external Seller service: {}", e.getMessage());
                    return Flux.empty();
                });
    }
}