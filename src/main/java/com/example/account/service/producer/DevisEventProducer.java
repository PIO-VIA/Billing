package com.example.account.service.producer;

import com.example.account.dto.response.DevisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevisEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String DEVIS_CREATED_TOPIC = "devis-created";
    private static final String DEVIS_UPDATED_TOPIC = "devis-updated";
    private static final String DEVIS_DELETED_TOPIC = "devis-deleted";
    private static final String DEVIS_ACCEPTED_TOPIC = "devis-accepted";

    public void publishDevisCreated(DevisResponse devisResponse) {
        log.info("Publication de l'événement devis créé: {}", devisResponse.getNumeroDevis());
        kafkaProducerService.sendMessage(DEVIS_CREATED_TOPIC, devisResponse.getIdDevis().toString(), devisResponse);
    }

    public void publishDevisUpdated(DevisResponse devisResponse) {
        log.info("Publication de l'événement devis mis à jour: {}", devisResponse.getNumeroDevis());
        kafkaProducerService.sendMessage(DEVIS_UPDATED_TOPIC, devisResponse.getIdDevis().toString(), devisResponse);
    }

    public void publishDevisDeleted(UUID devisId) {
        log.info("Publication de l'événement devis supprimé: {}", devisId);
        kafkaProducerService.sendMessage(DEVIS_DELETED_TOPIC, devisId.toString(), devisId);
    }

    public void publishDevisAccepted(DevisResponse devisResponse) {
        log.info("Publication de l'événement devis accepté: {}", devisResponse.getNumeroDevis());
        kafkaProducerService.sendMessage(DEVIS_ACCEPTED_TOPIC, devisResponse.getIdDevis().toString(), devisResponse);
    }
}
