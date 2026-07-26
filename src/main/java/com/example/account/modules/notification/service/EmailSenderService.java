package com.example.account.modules.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Single point of contact with Kernel's notification-core EMAIL channel
 * (POST /api/notifications/deliveries). Every other email-sending service in
 * this app should build its own subject/body (templating, attachments it needs
 * to handle itself — the Kernel endpoint has no attachment support) and then
 * call {@link #sendEmail} here rather than touching JavaMailSender or the
 * Kernel WebClient directly.
 */
@Service
@Slf4j
public class EmailSenderService {

    private final WebClient notificationKernelWebClient;

    public EmailSenderService(@Qualifier("notificationKernelWebClient") WebClient notificationKernelWebClient) {
        this.notificationKernelWebClient = notificationKernelWebClient;
    }

    public Mono<Void> sendEmail(String recipientAddress, String subject, String body) {
        return sendEmail(recipientAddress, subject, body, Map.of());
    }

    public Mono<Void> sendEmail(String recipientAddress, String subject, String body, Map<String, String> metadata) {
        return sendEmail(null, recipientAddress, subject, body, metadata);
    }

    /** Overload for callers that already know the target organization explicitly
     * (e.g. an internal API called by another service) rather than relying on the
     * caller's own request context. */
    public Mono<Void> sendEmail(UUID organizationId, String recipientAddress, String subject, String body) {
        return sendEmail(organizationId, recipientAddress, subject, body, Map.of());
    }

    public Mono<Void> sendEmail(UUID organizationId, String recipientAddress, String subject, String body,
            Map<String, String> metadata) {
        if (recipientAddress == null || recipientAddress.isBlank()) {
            log.warn("Skipping email send: no recipient address (subject '{}')", subject);
            return Mono.empty();
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("recipientAddress", recipientAddress);
        payload.put("channel", "EMAIL");
        payload.put("subject", subject == null ? "" : subject);
        payload.put("body", body);
        payload.put("metadata", metadata == null ? Map.of() : metadata);

        WebClient.RequestBodySpec request = notificationKernelWebClient.post()
                .uri("/api/notifications/deliveries");
        if (organizationId != null) {
            request = request.header("X-Organization-Id", organizationId.toString());
        }
        return request.bodyValue(payload)
                .retrieve()
                .bodyToMono(NotificationDeliveryResponse.class)
                .doOnNext(delivery -> {
                    if ("FAILED".equals(delivery.status())) {
                        log.warn("Notification delivery {} to {} failed: {}", delivery.id(), recipientAddress,
                                delivery.errorMessage());
                    } else {
                        log.info("Notification delivery {} to {} recorded as {}", delivery.id(), recipientAddress,
                                delivery.status());
                    }
                })
                .doOnError(err -> log.error("Failed to call Kernel notification-core for {}: {}", recipientAddress,
                        err.getMessage()))
                .then();
    }

    private record NotificationDeliveryResponse(String id, String status, String errorMessage) {
    }
}
