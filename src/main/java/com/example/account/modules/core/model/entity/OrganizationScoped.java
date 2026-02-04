package com.example.account.modules.core.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

/**
 * Base class for all entities that belong to an organization (multi-tenant entities).
 */
@Getter
@Setter
public abstract class OrganizationScoped {

    @Column("organization_id")
    private UUID organizationId;
}
