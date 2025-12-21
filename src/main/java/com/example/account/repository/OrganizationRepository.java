package com.example.account.repository;

import com.example.account.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Organization entity operations.
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    /**
     * Find organization by unique code.
     */
    Optional<Organization> findByCode(String code);

    /**
     * Check if organization code exists.
     */
    boolean existsByCode(String code);

    /**
     * Find active organizations.
     */
    List<Organization> findByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find organization by code (active only).
     */
    Optional<Organization> findByCodeAndIsActiveTrueAndDeletedAtIsNull(String code);

    /**
     * Find organizations by country.
     */
    List<Organization> findByCountryAndDeletedAtIsNull(String country);

    /**
     * Search organizations by name (case-insensitive).
     */
    @Query("SELECT o FROM Organization o WHERE " +
           "(LOWER(o.shortName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.longName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "o.deletedAt IS NULL")
    List<Organization> searchByName(@Param("query") String query);

    /**
     * Find organizations for a specific user.
     */
    @Query("SELECT DISTINCT o FROM Organization o " +
           "JOIN o.userOrganizations uo " +
           "WHERE uo.user.id = :userId AND uo.isActive = true AND uo.leftAt IS NULL " +
           "AND o.isActive = true AND o.deletedAt IS NULL")
    List<Organization> findActiveOrganizationsByUserId(@Param("userId") UUID userId);

    /**
     * Count active organizations for a user.
     */
    @Query("SELECT COUNT(DISTINCT o) FROM Organization o " +
           "JOIN o.userOrganizations uo " +
           "WHERE uo.user.id = :userId AND uo.isActive = true AND uo.leftAt IS NULL " +
           "AND o.isActive = true AND o.deletedAt IS NULL")
    long countActiveOrganizationsByUserId(@Param("userId") UUID userId);
}
