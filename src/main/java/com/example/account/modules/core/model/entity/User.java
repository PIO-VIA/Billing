package com.example.account.modules.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User entity for authentication and organization access management.
 * Represents a system user who can belong to multiple organizations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    @Column("id")
    private UUID id;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("is_active")
    private Boolean isActive = true;

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

    public void addOrganization(UserOrganization userOrganization) {
        userOrganizations.add(userOrganization);
        // userOrganization.setUser(this); // Cannot set circular ref in R2DBC
    }

    public void removeOrganization(UserOrganization userOrganization) {
        userOrganizations.remove(userOrganization);
        // userOrganization.setUser(null);
    }
}
