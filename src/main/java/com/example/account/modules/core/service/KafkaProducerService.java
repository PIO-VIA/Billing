package com.example.account.modules.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, String key, Object message) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message envoyé avec succès au topic [{}] avec clé [{}]: offset={}, partition={}",
                    topic, key, result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
            } else {
                log.error("Échec de l'envoi du message au topic [{}] avec clé [{}]: {}",
                    topic, key, ex.getMessage());
            }
        });
    }

    public void sendMessage(String topic, Object message) {
        sendMessage(topic, null, message);
    }
}
