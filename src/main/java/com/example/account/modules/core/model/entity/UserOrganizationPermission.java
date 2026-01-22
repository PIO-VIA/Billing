package com.example.account.modules.core.model.entity;

import com.example.account.modules.core.model.enums.Permission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_organization_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrganizationPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_organization_id", nullable = false)
    private UserOrganization userOrganization;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false)
    private Permission permission;

    /**
     * Optional expiration date for the permission.
     */
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_active", nullable = false)
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
