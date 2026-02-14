package com.example.account.modules.core.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base class for all entities that belong to an organization (multi-tenant entities).
 * In R2DBC, filtering must be done at the repository/service level.
 *
 * Usage:
 * - Extend this class in all business entities (Client, Facture, Produit, etc.)
 * - Organization filtering should be applied in repository queries
 * - Use WHERE organization_id = :organizationId in custom queries
 */
@Getter
@Setter
public abstract class OrganizationScoped {

    /**
     * Organization ID - tenant discriminator.
     * All queries should filter by this column for multi-tenancy.
     */
    private UUID organizationId;

    /**
     * Index for performance optimization.
     * Concrete entities should add indexes on (organization_id + frequently queried columns).
     */
}
