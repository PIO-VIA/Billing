package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.UserOrganization;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for UserOrganization entity operations.
 */
@Repository
public interface UserOrganizationRepository extends R2dbcRepository<UserOrganization, UUID> {
    
    Flux<UserOrganization> findByUserId(UUID userId);
    
    Flux<UserOrganization> findByOrganizationId(UUID organizationId);
    
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND organization_id = :organizationId")
    Mono<UserOrganization> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);
    
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND is_active = true")
    Flux<UserOrganization> findActiveByUserId(UUID userId);
    
    @Query("SELECT * FROM user_organizations WHERE user_id = :userId AND is_default = true")
    Mono<UserOrganization> findDefaultByUserId(UUID userId);
    
    Mono<Boolean> existsByUserIdAndOrganizationId(UUID userId, UUID organizationId);
}
