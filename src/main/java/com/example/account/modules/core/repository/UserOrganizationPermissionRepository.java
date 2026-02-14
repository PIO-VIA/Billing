package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.UserOrganizationPermission;
import com.example.account.modules.core.model.enums.Permission;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for UserOrganizationPermission entity operations.
 */
@Repository
public interface UserOrganizationPermissionRepository extends R2dbcRepository<UserOrganizationPermission, UUID> {
    
    Flux<UserOrganizationPermission> findByUserOrganizationId(UUID userOrganizationId);
    
    @Query("SELECT * FROM user_organization_permissions WHERE user_organization_id = :userOrganizationId AND is_active = true")
    Flux<UserOrganizationPermission> findActiveByUserOrganizationId(UUID userOrganizationId);
    
    @Query("SELECT * FROM user_organization_permissions WHERE user_organization_id = :userOrganizationId AND permission = :permission")
    Mono<UserOrganizationPermission> findByUserOrganizationIdAndPermission(UUID userOrganizationId, Permission permission);
    
    Mono<Boolean> existsByUserOrganizationIdAndPermission(UUID userOrganizationId, Permission permission);
}
