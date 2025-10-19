package com.example.account.service.producer;

import com.example.account.dto.response.FactureResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactureEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String FACTURE_CREATED_TOPIC = "facture-created";
    private static final String FACTURE_UPDATED_TOPIC = "facture-updated";
    private static final String FACTURE_DELETED_TOPIC = "facture-deleted";
    private static final String FACTURE_PAID_TOPIC = "facture-paid";

    public void publishFactureCreated(FactureResponse factureResponse) {
        log.info("Publication de l'événement facture créée: {}", factureResponse.getNumeroFacture());
        kafkaProducerService.sendMessage(FACTURE_CREATED_TOPIC, factureResponse.getIdFacture().toString(), factureResponse);
    }

    public void publishFactureUpdated(FactureResponse factureResponse) {
        log.info("Publication de l'événement facture mise à jour: {}", factureResponse.getNumeroFacture());
        kafkaProducerService.sendMessage(FACTURE_UPDATED_TOPIC, factureResponse.getIdFacture().toString(), factureResponse);
    }

    public void publishFactureDeleted(UUID factureId) {
        log.info("Publication de l'événement facture supprimée: {}", factureId);
        kafkaProducerService.sendMessage(FACTURE_DELETED_TOPIC, factureId.toString(), factureId);
    }

    public void publishFacturePaid(FactureResponse factureResponse) {
        log.info("Publication de l'événement facture payée: {}", factureResponse.getNumeroFacture());
        kafkaProducerService.sendMessage(FACTURE_PAID_TOPIC, factureResponse.getIdFacture().toString(), factureResponse);
    }
}
