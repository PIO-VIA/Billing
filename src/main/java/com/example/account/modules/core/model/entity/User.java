package com.example.account.modules.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User entity for authentication and organization access management.
 * Represents a system user who can belong to multiple organizations.
 * 
 * Note: In R2DBC, the @OneToMany relationship with UserOrganization is not supported.
 * User organizations should be loaded separately via UserOrganizationRepository.
 */
@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column("id")
    private UUID id;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    /**
     * BCrypt hashed password.
     * Should be handled by Spring Security PasswordEncoder.
     */
    @Column("password")
    private String password;

    /**
     * Indicates if the user account is active.
     * Inactive users cannot authenticate.
     */
    @Column("is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Soft delete support - marks user as deleted without removing from database.
     */
    @Column("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Helper method to check if user is deleted.
     */
    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }
}

