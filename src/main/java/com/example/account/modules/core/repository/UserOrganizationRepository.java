package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository for UserOrganization association entity.
 */
@Repository
public interface UserOrganizationRepository extends R2dbcRepository<UserOrganization, UUID> {

    /**
     * Find user's membership in a specific organization.
     */
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND organization_id = :organizationId")
    Mono<UserOrganization> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    /**
     * Find active membership.
     */
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND organization_id = :organizationId AND is_active = true AND left_at IS NULL")
    Mono<UserOrganization> findActiveByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    /**
     * Find user's default organization.
     */
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND is_default = true AND is_active = true AND left_at IS NULL")
    Mono<UserOrganization> findDefaultByUserId(UUID userId);

    /**
     * Find all active memberships for a user.
     */
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND is_active = true AND left_at IS NULL")
    Flux<UserOrganization> findActiveByUserId(UUID userId);

    /**
     * Find all active members of an organization.
     */
    @Query("SELECT * FROM user_organizations WHERE organization_id = :organizationId AND is_active = true AND left_at IS NULL")
    Flux<UserOrganization> findActiveByOrganizationId(UUID organizationId);

    /**
     * Check if user has access to organization.
     */
    @Query("SELECT COUNT(*) > 0 FROM user_organizations WHERE user_id = :userId AND organization_id = :organizationId AND is_active = true AND left_at IS NULL")
    Mono<Boolean> existsActiveByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    /**
     * Check if user has at least the specified role in organization.
     */
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND organization_id = :organizationId AND is_active = true AND left_at IS NULL")
    Mono<UserOrganization> findActiveRoleByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    /**
     * Find organization owners.
     */
    @Query("SELECT * FROM user_organizations WHERE organization_id = :organizationId AND role = 'OWNER' AND is_active = true AND left_at IS NULL")
    Flux<UserOrganization> findOwnersByOrganizationId(UUID organizationId);

    /**
     * Count active members of an organization.
     */
    @Query("SELECT COUNT(*) FROM user_organizations WHERE organization_id = :organizationId AND is_active = true AND left_at IS NULL")
    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    /**
     * Find all users with a specific role in an organization.
     */
    @Query("SELECT * FROM user_organizations WHERE organization_id = :organizationId AND role = :role")
    Flux<UserOrganization> findByOrganizationIdAndRole(UUID organizationId, OrganizationRole role);
}
