package com.example.account.modules.notification.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateLiveNotificationRequest {

    private List<UUID> contactIds;

    /** How long to batch pending invoices before flushing the notification. */
    @Positive
    private long delayMinutes;

    /** Populated server-side from the caller's org context, not accepted from the request body. */
    private UUID organizationId;
}
