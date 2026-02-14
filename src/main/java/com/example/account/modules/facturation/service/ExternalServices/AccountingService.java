package com.example.account.modules.facturation.service.ExternalServices;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;

import com.example.account.modules.facturation.model.entity.Facture;
import com.example.account.modules.facturation.repository.FactureRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingService {

    private final WebClient.Builder webClientBuilder;
    private final FactureRepository factureRepository;

    @Value("${comops.accounting_back.ip}")
    private String accountingBackURL;

    /**
     * Sends a specific Facture to the external accounting backend via POST.
     */
    public Mono<Void> sendFactureData(UUID factureId) {
        String url = String.format("http://%s/api/accounting/invoices/sale", accountingBackURL);

        return factureRepository.findById(factureId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Facture does not exist")))
                .flatMap(facture -> {
                    return webClientBuilder.build()
                            .post()
                            .uri(url)
                            .header("X-Tenant-ID", "550e8400-e29b-41d4-a716-446655440000") // TODO: Use real org id if needed
                            .bodyValue(facture)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, response -> 
                                response.bodyToMono(String.class)
                                        .flatMap(errorBody -> Mono.error(new Exception("External service communication failed: " + errorBody))))
                            .bodyToMono(Facture.class)
                            .doOnSuccess(res -> log.info("Facture sent successfully to accounting!"))
                            .then();
                })
                .onErrorMap(e -> new Exception("Accounting sync failed: " + e.getMessage()));
    }
}