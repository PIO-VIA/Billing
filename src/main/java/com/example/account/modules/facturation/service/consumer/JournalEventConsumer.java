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
public class JournalEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "journal-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJournalCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - journal-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement journal créé
            // Exemple: envoyer une notification, mettre à jour un cache, etc.

            acknowledgment.acknowledge();
            log.info("Événement journal-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement journal-created: {}", e.getMessage(), e);
            // Ne pas acknowledge en cas d'erreur pour retraiter le message
        }
    }

    @KafkaListener(topics = "journal-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJournalUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - journal-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement journal mis à jour

            acknowledgment.acknowledge();
            log.info("Événement journal-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement journal-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "journal-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJournalDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - journal-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement journal supprimé

            acknowledgment.acknowledge();
            log.info("Événement journal-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement journal-deleted: {}", e.getMessage(), e);
        }
    }
}
