package com.example.account.repository;

import com.example.account.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email.
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Check if username exists.
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Find active user by username.
     */
    Optional<User> findByUsernameAndIsActiveTrue(String username);

    /**
     * Find active user by email.
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /**
     * Find user with organizations eagerly loaded.
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userOrganizations uo LEFT JOIN FETCH uo.organization WHERE u.id = :userId")
    Optional<User> findByIdWithOrganizations(@Param("userId") UUID userId);

    /**
     * Find user by username with organizations eagerly loaded.
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userOrganizations uo LEFT JOIN FETCH uo.organization WHERE u.username = :username")
    Optional<User> findByUsernameWithOrganizations(@Param("username") String username);
}
