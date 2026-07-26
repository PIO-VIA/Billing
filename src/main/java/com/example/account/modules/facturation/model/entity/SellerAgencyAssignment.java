package com.example.account.modules.facturation.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Local record of which agency a seller is assigned to, owned entirely by
 * Billing. sales-core's own /api/sellers/{id}/agency endpoint needs an
 * internal Kernel round-trip (its own service-account login) that 401s
 * whenever KERNEL_BASE_URL isn't set in that deployment's environment, so
 * this assignment is validated and stored here instead — Billing already has
 * a working Kernel connection (see AgencyKernelAdapter) and doesn't need to
 * go through sales-core for it at all.
 */
@Table("seller_agency_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerAgencyAssignment {

    @Id
    @Column("id")
    private UUID id;

    @Column("seller_id")
    private UUID sellerId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("agency_id")
    private UUID agencyId;

    @Column("agency_name")
    private String agencyName;

    @Column("agency_email")
    private String agencyEmail;

    @Column("agency_phone")
    private String agencyPhone;

    @Column("agency_city")
    private String agencyCity;

    @Column("agency_address")
    private String agencyAddress;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
