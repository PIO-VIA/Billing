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
public class ClientEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "client-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClientCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - client-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement client créé
            // Exemple: envoyer une notification, mettre à jour un cache, etc.

            acknowledgment.acknowledge();
            log.info("Événement client-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement client-created: {}", e.getMessage(), e);
            // Ne pas acknowledge en cas d'erreur pour retraiter le message
        }
    }

    @KafkaListener(topics = "client-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClientUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - client-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement client mis à jour

            acknowledgment.acknowledge();
            log.info("Événement client-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement client-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "client-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeClientDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - client-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement client supprimé

            acknowledgment.acknowledge();
            log.info("Événement client-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement client-deleted: {}", e.getMessage(), e);
        }
    }
}
