package com.example.account.service.producer;

import com.example.account.dto.response.TaxeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxeEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String TAXE_CREATED_TOPIC = "taxe-created";
    private static final String TAXE_UPDATED_TOPIC = "taxe-updated";
    private static final String TAXE_DELETED_TOPIC = "taxe-deleted";

    public void publishTaxeCreated(TaxeResponse taxeResponse) {
        try {
            log.info("Publication de l'événement Taxe créée: {}", taxeResponse.getIdTaxe());
            kafkaProducerService.sendMessage(TAXE_CREATED_TOPIC, taxeResponse);
            log.info("Événement Taxe créée publié avec succès pour: {}", taxeResponse.getIdTaxe());
        } catch (Exception e) {
            log.error("Erreur lors de la publication de l'événement Taxe créée: {}", e.getMessage(), e);
        }
    }

    public void publishTaxeUpdated(TaxeResponse taxeResponse) {
        try {
            log.info("Publication de l'événement Taxe mise à jour: {}", taxeResponse.getIdTaxe());
            kafkaProducerService.sendMessage(TAXE_UPDATED_TOPIC, taxeResponse);
            log.info("Événement Taxe mise à jour publié avec succès pour: {}", taxeResponse.getIdTaxe());
        } catch (Exception e) {
            log.error("Erreur lors de la publication de l'événement Taxe mise à jour: {}", e.getMessage(), e);
        }
    }

    public void publishTaxeDeleted(UUID taxeId) {
        try {
            log.info("Publication de l'événement Taxe supprimée: {}", taxeId);
            kafkaProducerService.sendMessage(TAXE_DELETED_TOPIC, taxeId.toString());
            log.info("Événement Taxe supprimée publié avec succès pour: {}", taxeId);
        } catch (Exception e) {
            log.error("Erreur lors de la publication de l'événement Taxe supprimée: {}", e.getMessage(), e);
        }
    }
}
