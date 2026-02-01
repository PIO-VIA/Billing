package com.example.account.modules.core.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;

/**
 * Base class for all entities that belong to an organization (multi-tenant entities).
 * Provides automatic organization filtering via Hibernate filters.
 *
 * Usage:
 * - Extend this class in all business entities (Client, Facture, Produit, etc.)
 * - Hibernate filter is automatically applied to all queries
 * - Filter is activated by OrganizationFilter on each request
 */
@MappedSuperclass
@FilterDef(
    name = "organizationFilter",
    parameters = @ParamDef(name = "organizationId", type = UUID.class)
)
@Filter(
    name = "organizationFilter",
    condition = "organization_id = :organizationId"
)
@Getter
@Setter
public abstract class OrganizationScoped {

    /**
     * Organization entity - tenant discriminator relationship.
     * All queries will be automatically filtered by the underlying column.
     */
  
    private UUID organizationId;

    /**
     * Helper method to get the organization ID.
     * Useful for logic that only needs the ID.
     */
    

    /**
     * Index for performance optimization.
     * Composite indexes should be created on (organization_id + frequently queried columns).
     */
    // Note: Concrete entities should add:
    // @Index(name = "idx_{entity}_org", columnList = "organization_id")
}
