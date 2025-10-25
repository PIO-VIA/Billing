package com.example.account.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemboursementEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String REMBOURSEMENT_CREATED_TOPIC = "remboursement-created";
    private static final String REMBOURSEMENT_UPDATED_TOPIC = "remboursement-updated";
    private static final String REMBOURSEMENT_DELETED_TOPIC = "remboursement-deleted";

    public void publishRemboursementCreated(Object remboursementResponse) {
        log.info("Publication de l'événement remboursement créé");
        kafkaProducerService.sendMessage(REMBOURSEMENT_CREATED_TOPIC, remboursementResponse);
    }

    public void publishRemboursementUpdated(Object remboursementResponse) {
        log.info("Publication de l'événement remboursement mis à jour");
        kafkaProducerService.sendMessage(REMBOURSEMENT_UPDATED_TOPIC, remboursementResponse);
    }

    public void publishRemboursementDeleted(UUID remboursementId) {
        log.info("Publication de l'événement remboursement supprimé: {}", remboursementId);
        kafkaProducerService.sendMessage(REMBOURSEMENT_DELETED_TOPIC, remboursementId.toString(), remboursementId);
    }
}
