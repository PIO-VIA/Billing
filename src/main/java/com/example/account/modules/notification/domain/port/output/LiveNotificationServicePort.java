package com.example.account.modules.notification.domain.port.output;

import com.example.account.modules.notification.dto.CreateLiveNotificationRequest;
import com.example.account.modules.notification.dto.LiveNotificationResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LiveNotificationServicePort {
    Mono<LiveNotificationResponse> create(CreateLiveNotificationRequest request);
    Mono<LiveNotificationResponse> updateDelay(UUID id, long delayMinutes);
    Mono<Void> enqueueFacture(UUID organizationId, UUID factureId);
}
