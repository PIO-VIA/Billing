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
public class RemboursementEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "remboursement-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRemboursementCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - remboursement-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement remboursement créé
            // Exemple: envoyer une notification, mettre à jour un cache, etc.

            acknowledgment.acknowledge();
            log.info("Événement remboursement-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement remboursement-created: {}", e.getMessage(), e);
            // Ne pas acknowledge en cas d'erreur pour retraiter le message
        }
    }

    @KafkaListener(topics = "remboursement-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRemboursementUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - remboursement-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement remboursement mis à jour

            acknowledgment.acknowledge();
            log.info("Événement remboursement-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement remboursement-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "remboursement-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRemboursementDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - remboursement-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement de l'événement remboursement supprimé

            acknowledgment.acknowledge();
            log.info("Événement remboursement-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement remboursement-deleted: {}", e.getMessage(), e);
        }
    }
}
