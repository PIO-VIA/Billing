package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserOrganization association entity.
 */
@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UUID> {

    /**
     * Find user's membership in a specific organization.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "WHERE uo.user.id = :userId AND uo.organization.id = :organizationId")
    Optional<UserOrganization> findByUserIdAndOrganizationId(
        @Param("userId") UUID userId,
        @Param("organizationId") UUID organizationId
    );

    /**
     * Find active membership.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "WHERE uo.user.id = :userId AND uo.organization.id = :organizationId " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    Optional<UserOrganization> findActiveByUserIdAndOrganizationId(
        @Param("userId") UUID userId,
        @Param("organizationId") UUID organizationId
    );

    /**
     * Find user's default organization.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "WHERE uo.user.id = :userId AND uo.isDefault = true " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    Optional<UserOrganization> findDefaultByUserId(@Param("userId") UUID userId);

    /**
     * Find all active memberships for a user.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "LEFT JOIN FETCH uo.organization " +
           "WHERE uo.user.id = :userId AND uo.isActive = true AND uo.leftAt IS NULL")
    List<UserOrganization> findActiveByUserId(@Param("userId") UUID userId);

    /**
     * Find all active members of an organization.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "LEFT JOIN FETCH uo.user " +
           "WHERE uo.organization.id = :organizationId " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    List<UserOrganization> findActiveByOrganizationId(@Param("organizationId") UUID organizationId);

    /**
     * Check if user has access to organization.
     */
    @Query("SELECT COUNT(uo) > 0 FROM UserOrganization uo " +
           "WHERE uo.user.id = :userId AND uo.organization.id = :organizationId " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    boolean existsActiveByUserIdAndOrganizationId(
        @Param("userId") UUID userId,
        @Param("organizationId") UUID organizationId
    );

    /**
     * Check if user has at least the specified role in organization.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "WHERE uo.user.id = :userId AND uo.organization.id = :organizationId " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    Optional<UserOrganization> findActiveRoleByUserIdAndOrganizationId(
        @Param("userId") UUID userId,
        @Param("organizationId") UUID organizationId
    );

    /**
     * Find organization owners.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "LEFT JOIN FETCH uo.user " +
           "WHERE uo.organization.id = :organizationId " +
           "AND uo.role = 'OWNER' " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    List<UserOrganization> findOwnersByOrganizationId(@Param("organizationId") UUID organizationId);

    /**
     * Count active members of an organization.
     */
    @Query("SELECT COUNT(uo) FROM UserOrganization uo " +
           "WHERE uo.organization.id = :organizationId " +
           "AND uo.isActive = true AND uo.leftAt IS NULL")
    long countActiveByOrganizationId(@Param("organizationId") UUID organizationId);

    /**
     * Find all users with a specific role in an organization.
     */
    @Query("SELECT uo FROM UserOrganization uo " +
           "LEFT JOIN FETCH uo.user " +
           "WHERE uo.organization.id = :organizationId " +
           "AND uo.role = :role")
    List<UserOrganization> findByOrganizationIdAndRole(
        @Param("organizationId") UUID organizationId,
        @Param("role") OrganizationRole role
    );
}
