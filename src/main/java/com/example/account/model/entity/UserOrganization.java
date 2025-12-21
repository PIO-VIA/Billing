package com.example.account.model.entity;

import com.example.account.model.enums.OrganizationRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Association entity linking Users to Organizations with role-based access.
 * This entity manages the many-to-many relationship between User and Organization.
 *
 * Future extensions:
 * - Fine-grained permissions (RBAC)
 * - Access expiration dates
 * - Invitation workflow status
 */
@Entity
@Table(
    name = "user_organizations",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "organization_id"})
    },
    indexes = {
        @Index(name = "idx_user_org_user", columnList = "user_id"),
        @Index(name = "idx_user_org_org", columnList = "organization_id"),
        @Index(name = "idx_user_org_role", columnList = "role")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_organization_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_organization_org"))
    private Organization organization;

    /**
     * User's role within this organization.
     * Determines permission level (future RBAC integration).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private OrganizationRole role = OrganizationRole.MEMBER;

    /**
     * Indicates if this is the user's default/primary organization.
     * Used for auto-context selection when no org is specified.
     */
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    /**
     * Indicates if the user's access to this organization is active.
     * Allows temporary suspension without deletion.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    /**
     * Timestamp when user was removed from organization (soft delete).
     */
    @Column(name = "left_at")
    private LocalDateTime leftAt;

    /**
     * Helper method to check if membership is active.
     */
    @Transient
    public boolean isActiveMembership() {
        return isActive && leftAt == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOrganization)) return false;
        UserOrganization that = (UserOrganization) o;
        return user != null && user.equals(that.user) &&
               organization != null && organization.equals(that.organization);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
