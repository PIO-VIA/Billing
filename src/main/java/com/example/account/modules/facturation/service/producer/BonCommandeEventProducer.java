package com.example.account.modules.facturation.service.producer;

import com.example.account.modules.core.service.KafkaProducerService;
import com.example.account.modules.facturation.dto.response.BonCommandeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonCommandeEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String BON_COMMANDE_CREATED_TOPIC = "bon-commande-created";
    private static final String BON_COMMANDE_UPDATED_TOPIC = "bon-commande-updated";
    private static final String BON_COMMANDE_DELETED_TOPIC = "bon-commande-deleted";

    public void publishBonCommandeCreated(BonCommandeResponse response) {
        log.info("Publication de l'événement bon de commande créé: {}", response.getNumeroCommande());
        kafkaProducerService.sendMessage(BON_COMMANDE_CREATED_TOPIC, response.getIdBonCommande().toString(), response);
    }

    public void publishBonCommandeUpdated(BonCommandeResponse response) {
        log.info("Publication de l'événement bon de commande mis à jour: {}", response.getNumeroCommande());
        kafkaProducerService.sendMessage(BON_COMMANDE_UPDATED_TOPIC, response.getIdBonCommande().toString(), response);
    }

    public void publishBonCommandeDeleted(UUID bonCommandeId) {
        log.info("Publication de l'événement bon de commande supprimé: {}", bonCommandeId);
        kafkaProducerService.sendMessage(BON_COMMANDE_DELETED_TOPIC, bonCommandeId.toString(), bonCommandeId);
    }
}
