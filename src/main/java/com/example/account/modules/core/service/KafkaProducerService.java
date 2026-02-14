package com.example.account.modules.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<SendResult<String, Object>> sendMessage(String topic, String key, Object message) {
        log.info("Envoi d'un message au topic [{}] avec clé [{}]", topic, key);
        
        return Mono.fromFuture(() -> kafkaTemplate.send(topic, key, message))
                .doOnSuccess(result -> log.info("Message envoyé avec succès au topic [{}] avec clé [{}]: offset={}, partition={}",
                        topic, key, result.getRecordMetadata().offset(), result.getRecordMetadata().partition()))
                .doOnError(ex -> log.error("Échec de l'envoi du message au topic [{}] avec clé [{}]: {}",
                        topic, key, ex.getMessage()));
    }

    public Mono<SendResult<String, Object>> sendMessage(String topic, Object message) {
        return sendMessage(topic, null, message);
    }
}
