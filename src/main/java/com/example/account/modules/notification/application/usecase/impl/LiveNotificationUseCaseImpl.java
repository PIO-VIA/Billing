package com.example.account.modules.notification.application.usecase.impl;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.notification.domain.port.input.LiveNotificationUseCase;
import com.example.account.modules.notification.domain.port.output.LiveNotificationServicePort;
import com.example.account.modules.notification.dto.CreateLiveNotificationRequest;
import com.example.account.modules.notification.dto.LiveNotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveNotificationUseCaseImpl implements LiveNotificationUseCase {

    private final LiveNotificationServicePort liveNotificationServicePort;

    @Override
    public Mono<LiveNotificationResponse> create(CreateLiveNotificationRequest request) {
        return ReactiveOrganizationContext.getOrganizationId()
                .flatMap(orgId -> {
                    request.setOrganizationId(orgId);
                    return liveNotificationServicePort.create(request);
                });
    }

    @Override
    public Mono<LiveNotificationResponse> updateDelay(UUID id, long delayMinutes) {
        return liveNotificationServicePort.updateDelay(id, delayMinutes);
    }

    @Override
    public Mono<Void> enqueueFacture(UUID organizationId, UUID factureId) {
        return liveNotificationServicePort.enqueueFacture(organizationId, factureId);
    }
}
