package com.example.account.modules.notification.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * A per-organization batching rule: queues facture ids (without ever touching
 * the factures table itself) and flushes them as one summarized Telegram
 * message to its contacts every delayMinutes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("live_notifications")
public class LiveNotification implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean newEntity = true;

    @Override
    public boolean isNew() {
        return newEntity;
    }

    private UUID organizationId;

    // Who gets flushed messages.
    private UUID[] contactIds;

    // Facture ids queued since the last flush.
    private UUID[] pendingFactureIds;

    private long delayMinutes;
    private Instant lastSentAt;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
}
