package com.example.account.modules.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class LiveNotificationResponse {
    private UUID id;
    private UUID organizationId;
    private List<UUID> contactIds;
    private List<UUID> pendingFactureIds;
    private long delayMinutes;
    private Instant lastSentAt;
    private boolean active;
}
