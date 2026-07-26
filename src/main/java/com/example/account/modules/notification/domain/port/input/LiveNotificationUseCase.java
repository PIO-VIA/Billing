package com.example.account.modules.notification.domain.port.input;

import com.example.account.modules.notification.dto.CreateLiveNotificationRequest;
import com.example.account.modules.notification.dto.LiveNotificationResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LiveNotificationUseCase {
    Mono<LiveNotificationResponse> create(CreateLiveNotificationRequest request);
    Mono<LiveNotificationResponse> updateDelay(UUID id, long delayMinutes);

    /** Queues a facture on every active live-notification rule for the organization. Called from facturation on invoice creation. */
    Mono<Void> enqueueFacture(UUID organizationId, UUID factureId);
}
