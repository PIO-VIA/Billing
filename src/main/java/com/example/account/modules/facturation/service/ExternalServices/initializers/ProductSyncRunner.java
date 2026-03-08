package com.example.account.modules.facturation.service.ExternalServices.initializers;



import com.example.account.modules.facturation.service.ExternalServices.ProductExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSyncRunner implements CommandLineRunner {

    private final ProductExternalService productExternalService;

    // Use the Organization ID from your Express Mock Server
    private static final String ORG_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

    @Override
    public void run(String... args) {
        log.info("Starting initial product synchronization for organization: {}", ORG_ID);

        UUID organizationId = UUID.fromString(ORG_ID);

        // We use fetchAndSaveProducts which returns a Flux
        productExternalService.fetchAndSaveProducts(organizationId)
                .subscribe(
                    response -> log.info("Successfully synced product: {}", response.getNomProduit()),
                    error -> log.error("Sync failed: {}", error.getMessage()),
                    () -> log.info("Initial product synchronization completed successfully!")
                );
    }
}