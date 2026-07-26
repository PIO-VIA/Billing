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

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
