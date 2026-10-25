package com.example.account.modules.notification.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.account.modules.notification.model.enums.NotificationChannel;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A per-organization batching rule: queues a snapshot of each invoice's
 * display fields (captured at enqueue time, not the factures table itself)
 * and flushes them as one summarized Telegram message to its contacts every
 * delayMinutes.
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

    // Invoice snapshots queued since the last flush.
    @Column("pending_invoices")
    private List<PendingInvoiceSummary> pendingInvoices;

    private long delayMinutes;
    private Instant lastSentAt;
    private boolean active;

    @Column("channel")
    @Builder.Default
    private NotificationChannel channel = NotificationChannel.TELEGRAM;

    private Instant createdAt;
    private Instant updatedAt;
}
