package com.example.account.service.consumer;

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
public class PaiementEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "paiement-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaiementCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - paiement-created: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement: mise à jour facture, enregistrement comptable, notification

            acknowledgment.acknowledge();
            log.info("Événement paiement-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement paiement-created: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "paiement-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaiementUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - paiement-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement paiement-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement paiement-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "paiement-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumePaiementDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - paiement-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement paiement-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement paiement-deleted: {}", e.getMessage(), e);
        }
    }
}
