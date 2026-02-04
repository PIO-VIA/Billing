package com.example.account.modules.core.model.entity;

import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.model.enums.Permission;
import com.example.account.modules.facturation.model.enums.SaleSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("user_organizations")
public class UserOrganization {

    @Id
    @Column("id")
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("role")
    private OrganizationRole role = OrganizationRole.MEMBER;

    @Column("is_default")
    private Boolean isDefault = false;

    @Column("is_active")
    private Boolean isActive = true;

    @Column("joined_at")
    private LocalDateTime joinedAt;

    @Column("left_at")
    private LocalDateTime leftAt;

    @Transient
    private Set<UserOrganizationPermission> permissions = new HashSet<>();

    @Column("permitted_sale_sizes")
    private String permittedSaleSizes;

    @Column("agency")
    private String agency;

    @Column("sale_point")
    private String salePoint;

    @Transient
    public boolean isActiveMembership() {
        return isActive && leftAt == null;
    }

    public void addPermission(UserOrganizationPermission permission) {
        permissions.add(permission);
        // permission.setUserOrganization(this); // Cannot set circular ref in R2DBC easily
    }

    public void removePermission(UserOrganizationPermission permission) {
        permissions.remove(permission);
        // permission.setUserOrganization(null);
    }

    @Transient
    public boolean hasPermission(Permission permission) {
        return permissions.stream()
                .anyMatch(p -> p.getPermission() == permission && p.isActive());
    }

    @Transient
    public Set<Permission> getActivePermissions() {
        return permissions.stream()
                .filter(UserOrganizationPermission::isActive)
                .map(UserOrganizationPermission::getPermission)
                .collect(Collectors.toSet());
    }

    public void setPermittedSaleSizesList(List<SaleSize> saleSizes) {
        if (saleSizes == null || saleSizes.isEmpty()) {
            this.permittedSaleSizes = null;
        } else {
            this.permittedSaleSizes = saleSizes.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
        }
    }

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
