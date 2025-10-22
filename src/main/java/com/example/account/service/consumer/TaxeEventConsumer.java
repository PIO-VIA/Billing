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
public class TaxeEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "taxe-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaxeCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - taxe-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement taxe créée
            // Exemple: envoyer une notification, mettre à jour un cache, etc.

            acknowledgment.acknowledge();
            log.info("Événement taxe-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement taxe-created: {}", e.getMessage(), e);
            // Ne pas acknowledge en cas d'erreur pour retraiter le message
        }
    }

    @KafkaListener(topics = "taxe-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaxeUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - taxe-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement taxe mis à jour

            acknowledgment.acknowledge();
            log.info("Événement taxe-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement taxe-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "taxe-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaxeDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - taxe-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement taxe supprimée

            acknowledgment.acknowledge();
            log.info("Événement taxe-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement taxe-deleted: {}", e.getMessage(), e);
        }
    }
}
