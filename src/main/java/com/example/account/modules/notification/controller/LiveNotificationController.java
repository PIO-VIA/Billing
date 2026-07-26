package com.example.account.modules.notification.controller;

import com.example.account.modules.notification.domain.port.input.LiveNotificationUseCase;
import com.example.account.modules.notification.dto.CreateLiveNotificationRequest;
import com.example.account.modules.notification.dto.LiveNotificationResponse;
import com.example.account.modules.notification.dto.UpdateDelayRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications/live-notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Live Notifications", description = "Batching rule (contacts + flush delay) for the caller's organization, proxied to sales-core")
public class LiveNotificationController {

    private final LiveNotificationUseCase liveNotificationUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a notification for the caller's organization: contacts + flush delay")
    public Mono<LiveNotificationResponse> create(@Valid @RequestBody CreateLiveNotificationRequest request) {
        return liveNotificationUseCase.create(request);
    }

    @PutMapping("/{id}/delay")
    @Operation(summary = "Change how long the notification batches invoices before flushing")
    public Mono<LiveNotificationResponse> updateDelay(@PathVariable UUID id, @Valid @RequestBody UpdateDelayRequest request) {
        return liveNotificationUseCase.updateDelay(id, request.getDelayMinutes());
    }
}
