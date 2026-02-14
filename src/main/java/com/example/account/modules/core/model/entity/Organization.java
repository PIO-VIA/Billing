package com.example.account.modules.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Organization entity representing an isolated workspace in the multi-tenant system.
 * Each organization has its own billing, accounting, and inventory data.
 * 
 * Note: In R2DBC, relationships like @OneToMany are not supported.
 * UserOrganizations should be loaded separately via repository queries.
 */
@Table("organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @Column("id")
    private UUID id;

    /**
     * Unique organization code (e.g., "ORG-001", "ACME").
     * Used for tenant identification and routing.
     */
    @Column("code")
    private String code;

    @Column("short_name")
    private String shortName;

    @Column("long_name")
    private String longName;

    @Column("description")
    private String description;

    /**
     * Logo URL or file path.
     */
    @Column("logo")
    private String logo;

    /**
     * Organization status flags.
     */
    @Column("is_active")
    private Boolean isActive = true;

    @Column("is_individual")
    private Boolean isIndividual = false;

    @Column("is_public")
    private Boolean isPublic = false;

    @Column("is_business")
    private Boolean isBusiness = true;

    /**
     * Location information.
     */
    @Column("country")
    private String country;

    @Column("city")
    private String city;

    @Column("address")
    private String address;

    /**
     * Localization settings (e.g., "fr_FR", "en_US").
     */
    @Column("localization")
    private String localization;

    /**
     * Business hours.
     */
    @Column("open_time")
    private LocalTime openTime;

    @Column("close_time")
    private LocalTime closeTime;

    /**
     * Contact information.
     */
    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("whatsapp")
    private String whatsapp;

    /**
     * JSON or comma-separated social network URLs.
     */
    @Column("social_networks")
    private String socialNetworks;

    /**
     * Financial information.
     */
    @Column("capital_share")
    private Double capitalShare;

    @Column("tax_number")
    private String taxNumber;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Soft delete support.
     */
    @Column("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Helper method to check if organization is deleted.
     */
    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
