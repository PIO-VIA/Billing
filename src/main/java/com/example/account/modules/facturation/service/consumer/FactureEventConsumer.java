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
public class FactureEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "facture-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFactureCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - facture-created: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement: génération PDF, envoi email, mise à jour statistiques, etc.

            acknowledgment.acknowledge();
            log.info("Événement facture-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement facture-created: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "facture-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFactureUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - facture-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement facture-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement facture-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "facture-paid", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFacturePaid(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - facture-paid: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement: notification client, mise à jour solde, comptabilité, etc.

            acknowledgment.acknowledge();
            log.info("Événement facture-paid traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement facture-paid: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "facture-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFactureDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - facture-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement facture-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement facture-deleted: {}", e.getMessage(), e);
        }
    }
}
