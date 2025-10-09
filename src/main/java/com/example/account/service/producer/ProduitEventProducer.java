package com.example.account.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduitEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String PRODUIT_CREATED_TOPIC = "produit-created";
    private static final String PRODUIT_UPDATED_TOPIC = "produit-updated";
    private static final String PRODUIT_DELETED_TOPIC = "produit-deleted";

    public void publishProduitCreated(Object produitResponse) {
        log.info("Publication de l'événement produit créé");
        kafkaProducerService.sendMessage(PRODUIT_CREATED_TOPIC, produitResponse);
    }

    public void publishProduitUpdated(Object produitResponse) {
        log.info("Publication de l'événement produit mis à jour");
        kafkaProducerService.sendMessage(PRODUIT_UPDATED_TOPIC, produitResponse);
    }

    public void publishProduitDeleted(UUID produitId) {
        log.info("Publication de l'événement produit supprimé: {}", produitId);
        kafkaProducerService.sendMessage(PRODUIT_DELETED_TOPIC, produitId.toString(), produitId);
    }
}
