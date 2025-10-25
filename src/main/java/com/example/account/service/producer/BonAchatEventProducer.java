package com.example.account.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonAchatEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String BON_ACHAT_CREATED_TOPIC = "bon-achat-created";
    private static final String BON_ACHAT_UPDATED_TOPIC = "bon-achat-updated";
    private static final String BON_ACHAT_DELETED_TOPIC = "bon-achat-deleted";

    public void publishBonAchatCreated(Object bonAchatResponse) {
        log.info("Publication de l'événement bon d'achat créé");
        kafkaProducerService.sendMessage(BON_ACHAT_CREATED_TOPIC, bonAchatResponse);
    }

    public void publishBonAchatUpdated(Object bonAchatResponse) {
        log.info("Publication de l'événement bon d'achat mis à jour");
        kafkaProducerService.sendMessage(BON_ACHAT_UPDATED_TOPIC, bonAchatResponse);
    }

    public void publishBonAchatDeleted(UUID bonAchatId) {
        log.info("Publication de l'événement bon d'achat supprimé: {}", bonAchatId);
        kafkaProducerService.sendMessage(BON_ACHAT_DELETED_TOPIC, bonAchatId.toString(), bonAchatId);
    }
}
