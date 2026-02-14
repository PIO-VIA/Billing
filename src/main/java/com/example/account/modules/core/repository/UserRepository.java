package com.example.account.modules.core.repository;

import com.example.account.modules.core.model.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Reactive repository for User entity operations.
 */
@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {

    /**
     * Find user by username.
     */
    Mono<User> findByUsername(String username);

    /**
     * Find user by email.
     */
    Mono<User> findByEmail(String email);

    /**
     * Find user by username or email.
     * Note: In R2DBC, we use SQL instead of JPQL.
     */
    @Query("SELECT * FROM users WHERE username = :identifier OR email = :identifier")
    Mono<User> findByUsernameOrEmail(String identifier);

    /**
     * Check if username exists.
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Check if email exists.
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Find active user by username.
     */
    Mono<User> findByUsernameAndIsActiveTrue(String username);

    /**
     * Find active user by email.
     */
    Mono<User> findByEmailAndIsActiveTrue(String email);

    /**
     * Find user by ID.
     * Note: In R2DBC, relationships are not eagerly loaded.
     * UserOrganizations should be loaded separately via UserOrganizationRepository.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    Mono<User> findByIdWithOrganizations(UUID userId);

    /**
     * Find user by username.
     * Note: In R2DBC, relationships are not eagerly loaded.
     * UserOrganizations should be loaded separately via UserOrganizationRepository.
     */
    @Query("SELECT * FROM users WHERE username = :username")
    Mono<User> findByUsernameWithOrganizations(String username);
}
