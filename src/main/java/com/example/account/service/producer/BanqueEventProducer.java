package com.example.account.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BanqueEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String BANQUE_CREATED_TOPIC = "banque-created";
    private static final String BANQUE_UPDATED_TOPIC = "banque-updated";
    private static final String BANQUE_DELETED_TOPIC = "banque-deleted";

    public void publishBanqueCreated(Object banqueResponse) {
        log.info("Publication de l'événement banque créée");
        kafkaProducerService.sendMessage(BANQUE_CREATED_TOPIC, banqueResponse);
    }

    public void publishBanqueUpdated(Object banqueResponse) {
        log.info("Publication de l'événement banque mise à jour");
        kafkaProducerService.sendMessage(BANQUE_UPDATED_TOPIC, banqueResponse);
    }

    public void publishBanqueDeleted(UUID banqueId) {
        log.info("Publication de l'événement banque supprimée: {}", banqueId);
        kafkaProducerService.sendMessage(BANQUE_DELETED_TOPIC, banqueId.toString(), banqueId);
    }
}
