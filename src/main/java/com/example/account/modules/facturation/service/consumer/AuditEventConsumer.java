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
public class AuditEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "audit", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement d'audit reçu: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            // Traitement: enregistrement dans base de données d'audit,
            // analyse de sécurité, génération de rapports, etc.

            acknowledgment.acknowledge();
            log.info("Événement d'audit traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement d'audit: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "notification", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeNotificationEvent(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement de notification reçu: key={}", key);
            log.debug("Contenu du message: {}", message);

            // Traitement: envoi email, SMS, push notification, etc.

            acknowledgment.acknowledge();
            log.info("Événement de notification traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement de notification: {}", e.getMessage(), e);
        }
    }
}
