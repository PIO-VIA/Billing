package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.Organization;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for Organization entity operations.
 */
@Repository
public interface OrganizationRepository extends R2dbcRepository<Organization, UUID> {
    
    Mono<Organization> findByCode(String code);
    
    Mono<Boolean> existsByCode(String code);
}
