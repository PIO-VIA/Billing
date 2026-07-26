package com.example.account.modules.notification.repository;

import com.example.account.modules.notification.model.entity.LiveNotification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface LiveNotificationRepository extends ReactiveCrudRepository<LiveNotification, UUID> {

    Flux<LiveNotification> findByOrganizationId(UUID organizationId);

    Flux<LiveNotification> findByActiveTrue();
}
