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
public class BanqueEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "banque-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBanqueCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - banque-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement banque créée
            // Exemple: envoyer une notification, mettre à jour un cache, etc.

            acknowledgment.acknowledge();
            log.info("Événement banque-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement banque-created: {}", e.getMessage(), e);
            // Ne pas acknowledge en cas d'erreur pour retraiter le message
        }
    }

    @KafkaListener(topics = "banque-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBanqueUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - banque-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement banque mise à jour

            acknowledgment.acknowledge();
            log.info("Événement banque-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement banque-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "banque-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBanqueDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - banque-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement banque supprimée

            acknowledgment.acknowledge();
            log.info("Événement banque-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement banque-deleted: {}", e.getMessage(), e);
        }
    }
}
