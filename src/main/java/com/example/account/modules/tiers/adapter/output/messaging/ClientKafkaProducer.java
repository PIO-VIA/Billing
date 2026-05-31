package com.example.account.modules.tiers.adapter.output.messaging;

import com.example.account.modules.tiers.domain.port.output.ClientEventPort;
import com.example.account.modules.tiers.dto.ClientResponse;
import com.example.account.modules.tiers.service.producer.ClientEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientKafkaProducer implements ClientEventPort {

    private final ClientEventProducer originalProducer;

    @Override
    public void publishClientCreated(ClientResponse client) {
        originalProducer.publishClientCreated(client);
    }

    @Override
    public void publishClientUpdated(ClientResponse client) {
        originalProducer.publishClientUpdated(client);
    }

    @Override
    public void publishClientDeleted(UUID clientId) {
        originalProducer.publishClientDeleted(clientId);
    }
}
