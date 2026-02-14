package com.example.account.modules.core.model.entity;

import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.facturation.model.enums.SaleSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Association entity linking Users to Organizations with role-based access.
 * This entity manages the many-to-many relationship between User and Organization.
 *
 * In R2DBC, relationships are represented by IDs.
 * Load User and Organization separately via their repositories.
 * Permissions should be loaded separately via UserOrganizationPermissionRepository.
 */
@Table("user_organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganization {

    @Id
    @Column("id")
    private UUID id;

    /**
     * Reference to the User.
     * Load User separately via UserRepository.
     */
    @Column("user_id")
    private UUID userId;

    /**
     * Reference to the Organization.
     * Load Organization separately via OrganizationRepository.
     */
    @Column("organization_id")
    private UUID organizationId;

    /**
     * User's role within this organization.
     * Determines permission level (future RBAC integration).
     */
    @Column("role")
    private OrganizationRole role = OrganizationRole.MEMBER;

    /**
     * Indicates if this is the user's default/primary organization.
     * Used for auto-context selection when no org is specified.
     */
    @Column("is_default")
    private Boolean isDefault = false;

    /**
     * Indicates if the user's access to this organization is active.
     * Allows temporary suspension without deletion.
     */
    @Column("is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column("joined_at")
    private LocalDateTime joinedAt;

    /**
     * Timestamp when user was removed from organization (soft delete).
     */
    @Column("left_at")
    private LocalDateTime leftAt;

    /**
     * Permitted sale sizes for sellers.
     * Stored as comma-separated values (e.g., "DETAIL,GROS").
     * Only relevant for users with SELLER role.
     */
    @Column("permitted_sale_sizes")
    private String permittedSaleSizes;

    /**
     * Agency/branch where this seller operates.
     * Only relevant for SELLER role.
     */
    @Column("agency")
    private String agency;

    /**
     * Sale point/cash register identifier.
     * Only relevant for SELLER role.
     */
    @Column("sale_point")
    private String salePoint;

    /**
     * Helper method to check if membership is active.
     */
    @Transient
    public boolean isActiveMembership() {
        return isActive && leftAt == null;
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
}
