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
 * Implements Persistable because the id is client-generated (UUID.randomUUID()
 * before insert) — same pattern as SellerAgencyAssignment/SellerAuthContext.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("contacts")
public class Contact implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean newEntity = true;

    @Override
    public boolean isNew() {
        return newEntity;
    }

    private String name;
    private String email;

    // Telegram chat id; null until the contact taps the bot's /start deep link.
    private String chatId;

    private UUID organizationId;

    // Embedded in the /start deep link; single use to claim a chatId.
    private String linkToken;

    // ContactStatus name: PENDING, ACTIVE, BLOCKED_BY_ADMIN, BLOCKED_BY_USER.
    // String column, converted at the service boundary — same convention as Seller.role.
    private String status;

    private Instant createdAt;
    private Instant updatedAt;
}
