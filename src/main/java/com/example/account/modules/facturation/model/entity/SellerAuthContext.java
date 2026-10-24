package com.example.account.modules.facturation.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Local record of a seller's POS quick-login PIN, created in Billing right after
 * the seller is provisioned via Kernel/sales-core (which doesn't hand back a PIN).
 */
@Table("seller_auth_contexts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerAuthContext {

    @Id
    @Column("id")
    private UUID id;

    @Column("seller_id")
    private UUID sellerId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("pin")
    private String pin;

    // Cached from the seller's last real Kernel login so PIN login — which
    // never talks to Kernel on its own — can mint a fresh accessToken via
    // /api/auth/refresh instead of leaving the frontend with no Kernel
    // session at all. Null until that seller logs in normally at least once.
    @Column("refresh_token")
    private String refreshToken;

    @Column("refresh_expires_at")
    private LocalDateTime refreshExpiresAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
