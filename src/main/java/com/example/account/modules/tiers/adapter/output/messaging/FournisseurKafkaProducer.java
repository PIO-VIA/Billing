package com.example.account.modules.tiers.adapter.output.messaging;

import com.example.account.modules.tiers.domain.port.output.FournisseurEventPort;
import com.example.account.modules.tiers.dto.FournisseurResponse;
import com.example.account.modules.tiers.service.producer.FournisseurEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FournisseurKafkaProducer implements FournisseurEventPort {

    private final FournisseurEventProducer originalProducer;

    @Override
    public void publishFournisseurCreated(FournisseurResponse fournisseur) {
        originalProducer.publishFournisseurCreated(fournisseur);
    }

    @Override
    public void publishFournisseurUpdated(FournisseurResponse fournisseur) {
        originalProducer.publishFournisseurUpdated(fournisseur);
    }

    @Override
    public void publishFournisseurDeleted(UUID fournisseurId) {
        originalProducer.publishFournisseurDeleted(fournisseurId);
    }
}
