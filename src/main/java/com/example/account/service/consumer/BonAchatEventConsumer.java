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
public class BonAchatEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "bon-achat-created", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBonAchatCreated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.RECEIVED_PARTITION) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - bon-achat-created: partition={}, offset={}, key={}", partition, offset, key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement bon-achat-created traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement bon-achat-created: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "bon-achat-updated", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBonAchatUpdated(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - bon-achat-updated: key={}", key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement bon-achat-updated traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement bon-achat-updated: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "bon-achat-deleted", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBonAchatDeleted(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {

        try {
            log.info("Événement reçu - bon-achat-deleted: key={}", key);
            log.debug("Contenu du message: {}", message);

            acknowledgment.acknowledge();
            log.info("Événement bon-achat-deleted traité avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du traitement de l'événement bon-achat-deleted: {}", e.getMessage(), e);
        }
    }
}
