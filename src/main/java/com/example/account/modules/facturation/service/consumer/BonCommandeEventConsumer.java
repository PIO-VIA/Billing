package com.example.account.modules.facturation.service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonCommandeEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "bon-commande-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBonCommandeCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - bon-commande-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement bon de commande créé

            acknowledgment.acknowledge();
            log.info("Événement bon-commande-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement bon-commande-created: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "bon-commande-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBonCommandeUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - bon-commande-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement bon de commande mis à jour

            acknowledgment.acknowledge();
            log.info("Événement bon-commande-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement bon-commande-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "bon-commande-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBonCommandeDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - bon-commande-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement bon de commande supprimé

            acknowledgment.acknowledge();
            log.info("Événement bon-commande-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement bon-commande-deleted: {}", e.getMessage(), e);
        }
    }
}
