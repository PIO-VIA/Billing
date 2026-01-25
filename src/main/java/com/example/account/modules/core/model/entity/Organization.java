package com.example.account.modules.core.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Organization entity representing an isolated workspace in the multi-tenant system.
 * Each organization has its own billing, accounting, and inventory data.
 */
@Entity
@Table(
    name = "organizations",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_org_code", columnList = "code"),
        @Index(name = "idx_org_active", columnList = "is_active"),
        @Index(name = "idx_org_country_city", columnList = "country, city")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Unique organization code (e.g., "ORG-001", "ACME").
     * Used for tenant identification and routing.
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "short_name", nullable = false, length = 100)
    private String shortName;

    @Column(name = "long_name", length = 255)
    private String longName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Logo URL or file path.
     */
    @Column(name = "logo", length = 500)
    private String logo;

    /**
     * Organization status flags.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_individual", nullable = false)
    private Boolean isIndividual = false;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "is_business", nullable = false)
    private Boolean isBusiness = true;

    /**
     * Location information.
     */
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * Localization settings (e.g., "fr_FR", "en_US").
     */
    @Column(name = "localization", length = 10)
    private String localization;

    /**
     * Business hours.
     */
    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    /**
     * Contact information.
     */
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "whatsapp", length = 50)
    private String whatsapp;

    /**
     * JSON or comma-separated social network URLs.
     */
    @Column(name = "social_networks", columnDefinition = "TEXT")
    private String socialNetworks;

    /**
     * Financial information.
     */
    @Column(name = "capital_share")
    private Double capitalShare;

    @Column(name = "tax_number", length = 100)
    private String taxNumber;

    /**
     * Many-to-many relationship with users.
     */
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserOrganization> userOrganizations = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Soft delete support.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Helper method to check if organization is deleted.
     */
    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Helper method to add user membership.
     */
    public void addUser(UserOrganization userOrganization) {
        userOrganizations.add(userOrganization);
        userOrganization.setOrganization(this);
    }

    /**
     * Helper method to remove user membership.
     */
    public void removeUser(UserOrganization userOrganization) {
        userOrganizations.remove(userOrganization);
        userOrganization.setOrganization(null);
    }
}
