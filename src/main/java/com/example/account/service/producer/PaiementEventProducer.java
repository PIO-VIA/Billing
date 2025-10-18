package com.example.account.service.producer;

import com.example.account.dto.response.PaiementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaiementEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String PAIEMENT_CREATED_TOPIC = "paiement-created";
    private static final String PAIEMENT_UPDATED_TOPIC = "paiement-updated";
    private static final String PAIEMENT_DELETED_TOPIC = "paiement-deleted";

    public void publishPaiementCreated(PaiementResponse paiementResponse) {
        log.info("Publication de l'événement paiement créé: {}", paiementResponse.getIdPaiement());
        kafkaProducerService.sendMessage(PAIEMENT_CREATED_TOPIC, paiementResponse.getIdPaiement().toString(), paiementResponse);
    }

    public void publishPaiementUpdated(PaiementResponse paiementResponse) {
        log.info("Publication de l'événement paiement mis à jour: {}", paiementResponse.getIdPaiement());
        kafkaProducerService.sendMessage(PAIEMENT_UPDATED_TOPIC, paiementResponse.getIdPaiement().toString(), paiementResponse);
    }

    public void publishPaiementDeleted(UUID paiementId) {
        log.info("Publication de l'événement paiement supprimé: {}", paiementId);
        kafkaProducerService.sendMessage(PAIEMENT_DELETED_TOPIC, paiementId.toString(), paiementId);
    }
}
