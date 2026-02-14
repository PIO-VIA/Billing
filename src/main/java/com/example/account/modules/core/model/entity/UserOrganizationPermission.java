package com.example.account.modules.core.model.entity;

import com.example.account.modules.core.model.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Permission entity for fine-grained access control.
 * In R2DBC, the UserOrganization relationship is represented by userOrganizationId.
 */
@Table("user_organization_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrganizationPermission {

    @Id
    @Column("id")
    private UUID id;

    /**
     * Reference to the UserOrganization.
     * Load UserOrganization separately via UserOrganizationRepository.
     */
    @Column("user_organization_id")
    private UUID userOrganizationId;

    @Column("permission")
    private Permission permission;

    /**
     * Optional expiration date for the permission.
     */
    @Column("expiry_date")
    private LocalDateTime expiryDate;

    @Column("is_active")
    @Builder.Default
    private boolean isActive = true;

    /**
     * Helper method to check if permission is effectively active.
     */
    @Transient
    public boolean isCurrentlyActive() {
        return isActive && (expiryDate == null || expiryDate.isAfter(LocalDateTime.now()));
    }
}
