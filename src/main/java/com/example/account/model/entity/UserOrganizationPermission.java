package com.example.account.model.entity;

import com.example.account.model.enums.Permission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Junction table linking users to specific permissions within an organization.
 * Enables fine-grained permission control per user per organization.
 *
 * Example: User Alice in Organization A can have CREATE_INVOICE permission,
 * but in Organization B she might only have VIEW_ANALYTICS permission.
 */
@Entity
@Table(
    name = "user_organization_permissions",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_org_permission",
            columnNames = {"user_organization_id", "permission"}
        )
    },
    indexes = {
        @Index(name = "idx_user_org_perm_user_org", columnList = "user_organization_id"),
        @Index(name = "idx_user_org_perm_permission", columnList = "permission")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the user-organization membership.
     * This links the permission to a specific user in a specific organization.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_organization_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_user_org_perm_user_org")
    )
    private UserOrganization userOrganization;

    /**
     * The specific permission granted to the user in this organization.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 50)
    private Permission permission;

    /**
     * Timestamp when this permission was granted.
     */
    @CreationTimestamp
    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;

    /**
     * Optional: Who granted this permission (for audit purposes).
     */
    @Column(name = "granted_by")
    private UUID grantedBy;

    /**
     * Optional: Expiration date for temporary permissions.
     * Null means the permission doesn't expire.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Helper method to check if permission is expired.
     */
    @Transient
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Helper method to check if permission is active.
     */
    @Transient
    public boolean isActive() {
        return !isExpired();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserOrganizationPermission)) return false;
        UserOrganizationPermission that = (UserOrganizationPermission) o;
        return userOrganization != null && userOrganization.equals(that.userOrganization) &&
               permission == that.permission;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
