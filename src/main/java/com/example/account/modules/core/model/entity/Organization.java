package com.example.account.modules.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Organization entity representing an isolated workspace in the multi-tenant system.
 * Each organization has its own billing, accounting, and inventory data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("organizations")
public class Organization {

    @Id
    @Column("id")
    private UUID id;

    @Column("code")
    private String code;

    @Column("short_name")
    private String shortName;

    @Column("long_name")
    private String longName;

    @Column("description")
    private String description;

    @Column("logo")
    private String logo;

    @Column("is_active")
    private Boolean isActive = true;

    @Column("is_individual")
    private Boolean isIndividual = false;

    @Column("is_public")
    private Boolean isPublic = false;

    @Column("is_business")
    private Boolean isBusiness = true;

    @Column("country")
    private String country;

    @Column("city")
    private String city;

    @Column("address")
    private String address;

    @Column("localization")
    private String localization;

    @Column("open_time")
    private LocalTime openTime;

    @Column("close_time")
    private LocalTime closeTime;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("whatsapp")
    private String whatsapp;

    @Column("social_networks")
    private String socialNetworks;

    @Column("capital_share")
    private Double capitalShare;

    @Column("tax_number")
    private String taxNumber;

    @Transient
    private Set<UserOrganization> userOrganizations = new HashSet<>();

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("deleted_at")
    private LocalDateTime deletedAt;

    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void addUser(UserOrganization userOrganization) {
        userOrganizations.add(userOrganization);
        // userOrganization.setOrganization(this); // Cannot set circular ref in R2DBC
    }

    public void removeUser(UserOrganization userOrganization) {
        userOrganizations.remove(userOrganization);
        // userOrganization.setOrganization(null);
    }
}
