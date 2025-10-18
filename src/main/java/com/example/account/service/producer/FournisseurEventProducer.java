package com.example.account.service.producer;

import com.example.account.dto.response.FournisseurResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FournisseurEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String FOURNISSEUR_CREATED_TOPIC = "fournisseur-created";
    private static final String FOURNISSEUR_UPDATED_TOPIC = "fournisseur-updated";
    private static final String FOURNISSEUR_DELETED_TOPIC = "fournisseur-deleted";

    public void publishFournisseurCreated(FournisseurResponse fournisseurResponse) {
        log.info("Publication de l'événement fournisseur créé: {}", fournisseurResponse.getIdFournisseur());
        kafkaProducerService.sendMessage(FOURNISSEUR_CREATED_TOPIC, fournisseurResponse.getIdFournisseur().toString(), fournisseurResponse);
    }

    public void publishFournisseurUpdated(FournisseurResponse fournisseurResponse) {
        log.info("Publication de l'événement fournisseur mis à jour: {}", fournisseurResponse.getIdFournisseur());
        kafkaProducerService.sendMessage(FOURNISSEUR_UPDATED_TOPIC, fournisseurResponse.getIdFournisseur().toString(), fournisseurResponse);
    }

    public void publishFournisseurDeleted(UUID fournisseurId) {
        log.info("Publication de l'événement fournisseur supprimé: {}", fournisseurId);
        kafkaProducerService.sendMessage(FOURNISSEUR_DELETED_TOPIC, fournisseurId.toString(), fournisseurId);
    }
}
