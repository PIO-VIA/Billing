package com.example.account.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User entity for authentication and organization access management.
 * Represents a system user who can belong to multiple organizations.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_email", columnList = "email")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * BCrypt hashed password.
     * Should be handled by Spring Security PasswordEncoder.
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * Indicates if the user account is active.
     * Inactive users cannot authenticate.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Many-to-many relationship with organizations.
     * A user can belong to multiple organizations, and an organization can have multiple users.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserOrganization> userOrganizations = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Soft delete support - marks user as deleted without removing from database.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Helper method to check if user is deleted.
     */
    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Helper method to add organization membership.
     */
    public void addOrganization(UserOrganization userOrganization) {
        userOrganizations.add(userOrganization);
        userOrganization.setUser(this);
    }

    /**
     * Helper method to remove organization membership.
     */
    public void removeOrganization(UserOrganization userOrganization) {
        userOrganizations.remove(userOrganization);
        userOrganization.setUser(null);
    }
}
