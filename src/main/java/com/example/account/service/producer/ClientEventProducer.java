package com.example.account.service.producer;

import com.example.account.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientEventProducer {

    private final KafkaProducerService kafkaProducerService;

    private static final String CLIENT_CREATED_TOPIC = "client-created";
    private static final String CLIENT_UPDATED_TOPIC = "client-updated";
    private static final String CLIENT_DELETED_TOPIC = "client-deleted";

    public void publishClientCreated(ClientResponse clientResponse) {
        log.info("Publication de l'événement client créé: {}", clientResponse.getIdClient());
        kafkaProducerService.sendMessage(CLIENT_CREATED_TOPIC, clientResponse.getIdClient().toString(), clientResponse);
    }

    public void publishClientUpdated(ClientResponse clientResponse) {
        log.info("Publication de l'événement client mis à jour: {}", clientResponse.getIdClient());
        kafkaProducerService.sendMessage(CLIENT_UPDATED_TOPIC, clientResponse.getIdClient().toString(), clientResponse);
    }

    public void publishClientDeleted(UUID clientId) {
        log.info("Publication de l'événement client supprimé: {}", clientId);
        kafkaProducerService.sendMessage(CLIENT_DELETED_TOPIC, clientId.toString(), clientId);
    }
}
