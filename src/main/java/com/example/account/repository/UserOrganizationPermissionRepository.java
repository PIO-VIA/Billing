package com.example.account.repository;

import com.example.account.model.entity.UserOrganizationPermission;
import com.example.account.model.enums.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing user-organization permissions.
 * Provides methods to query and manage fine-grained permissions.
 */
@Repository
public interface UserOrganizationPermissionRepository extends JpaRepository<UserOrganizationPermission, UUID> {

    /**
     * Find all permissions for a user-organization relationship.
     * @param userOrganizationId The user-organization ID
     * @return List of permissions
     */
    @Query("SELECT uop FROM UserOrganizationPermission uop WHERE uop.userOrganization.id = :userOrganizationId")
    List<UserOrganizationPermission> findByUserOrganizationId(@Param("userOrganizationId") UUID userOrganizationId);

    /**
     * Find all active (non-expired) permissions for a user-organization relationship.
     * @param userOrganizationId The user-organization ID
     * @return List of active permissions
     */
    @Query("SELECT uop FROM UserOrganizationPermission uop " +
           "WHERE uop.userOrganization.id = :userOrganizationId " +
           "AND (uop.expiresAt IS NULL OR uop.expiresAt > CURRENT_TIMESTAMP)")
    List<UserOrganizationPermission> findActiveByUserOrganizationId(@Param("userOrganizationId") UUID userOrganizationId);

    /**
     * Find a specific permission for a user-organization relationship.
     * @param userOrganizationId The user-organization ID
     * @param permission The permission to find
     * @return Optional containing the permission if found
     */
    @Query("SELECT uop FROM UserOrganizationPermission uop " +
           "WHERE uop.userOrganization.id = :userOrganizationId " +
           "AND uop.permission = :permission")
    Optional<UserOrganizationPermission> findByUserOrganizationIdAndPermission(
            @Param("userOrganizationId") UUID userOrganizationId,
            @Param("permission") Permission permission
    );

    /**
     * Find all permissions for a user across all their organizations.
     * @param userId The user ID
     * @return List of permissions
     */
    @Query("SELECT uop FROM UserOrganizationPermission uop " +
           "WHERE uop.userOrganization.user.id = :userId")
    List<UserOrganizationPermission> findByUserId(@Param("userId") UUID userId);

    /**
     * Find all permissions for a user in a specific organization.
     * @param userId The user ID
     * @param organizationId The organization ID
     * @return List of permissions
     */
    @Query("SELECT uop FROM UserOrganizationPermission uop " +
           "WHERE uop.userOrganization.user.id = :userId " +
           "AND uop.userOrganization.organization.id = :organizationId")
    List<UserOrganizationPermission> findByUserIdAndOrganizationId(
            @Param("userId") UUID userId,
            @Param("organizationId") UUID organizationId
    );

    /**
     * Check if a user has a specific permission in an organization.
     * @param userId The user ID
     * @param organizationId The organization ID
     * @param permission The permission to check
     * @return true if the user has the permission and it's active
     */
    @Query("SELECT COUNT(uop) > 0 FROM UserOrganizationPermission uop " +
           "WHERE uop.userOrganization.user.id = :userId " +
           "AND uop.userOrganization.organization.id = :organizationId " +
           "AND uop.permission = :permission " +
           "AND (uop.expiresAt IS NULL OR uop.expiresAt > CURRENT_TIMESTAMP)")
    boolean hasPermission(
            @Param("userId") UUID userId,
            @Param("organizationId") UUID organizationId,
            @Param("permission") Permission permission
    );

    /**
     * Delete all permissions for a user-organization relationship.
     * @param userOrganizationId The user-organization ID
     */
    void deleteByUserOrganizationId(UUID userOrganizationId);
}
