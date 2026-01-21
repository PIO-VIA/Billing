package com.example.account.model.entity;

import com.example.account.model.enums.OrganizationRole;
import com.example.account.model.enums.Permission;
import com.example.account.model.enums.SaleSize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * Fine-grained permissions assigned to this user in this organization.
     * Enables granular control over what actions the user can perform.
     */
    @OneToMany(mappedBy = "userOrganization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserOrganizationPermission> permissions = new HashSet<>();

    /**
     * Permitted sale sizes for sellers.
     * Stored as comma-separated values (e.g., "DETAIL,GROS").
     * Only relevant for users with SELLER role.
     */
    @Column(name = "permitted_sale_sizes", length = 500)
    private String permittedSaleSizes;

    /**
     * Agency/branch where this seller operates.
     * Only relevant for SELLER role.
     */
    @Column(name = "agency", length = 100)
    private String agency;

    /**
     * Sale point/cash register identifier.
     * Only relevant for SELLER role.
     */
    @Column(name = "sale_point", length = 100)
    private String salePoint;

    /**
     * Helper method to check if membership is active.
     */
    @Transient
    public boolean isActiveMembership() {
        return isActive && leftAt == null;
    }

    /**
     * Helper method to add a permission to this user-organization relationship.
     */
    public void addPermission(UserOrganizationPermission permission) {
        permissions.add(permission);
        permission.setUserOrganization(this);
    }

    /**
     * Helper method to remove a permission from this user-organization relationship.
     */
    public void removePermission(UserOrganizationPermission permission) {
        permissions.remove(permission);
        permission.setUserOrganization(null);
    }

    /**
     * Check if user has a specific permission in this organization.
     * @param permission The permission to check
     * @return true if user has the permission and it's active (not expired)
     */
    @Transient
    public boolean hasPermission(Permission permission) {
        return permissions.stream()
                .anyMatch(p -> p.getPermission() == permission && p.isActive());
    }

    /**
     * Get all active permissions for this user in this organization.
     * @return Set of active permissions
     */
    @Transient
    public Set<Permission> getActivePermissions() {
        return permissions.stream()
                .filter(UserOrganizationPermission::isActive)
                .map(UserOrganizationPermission::getPermission)
                .collect(Collectors.toSet());
    }

    /**
     * Set permitted sale sizes from a list of SaleSize enums.
     * @param saleSizes List of sale sizes
     */
    public void setPermittedSaleSizesList(List<SaleSize> saleSizes) {
        if (saleSizes == null || saleSizes.isEmpty()) {
            this.permittedSaleSizes = null;
        } else {
            this.permittedSaleSizes = saleSizes.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
        }
    }

    /**
     * Get permitted sale sizes as a list of SaleSize enums.
     * @return List of permitted sale sizes
     */
    @Transient
    public List<SaleSize> getPermittedSaleSizesList() {
        if (permittedSaleSizes == null || permittedSaleSizes.isEmpty()) {
            return List.of();
        }
        return List.of(permittedSaleSizes.split(",")).stream()
                .map(SaleSize::valueOf)
                .collect(Collectors.toList());
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
