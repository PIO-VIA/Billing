package com.example.account.modules.core.service;

import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.core.model.entity.Organization;
import com.example.account.modules.core.model.entity.OrganizationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper class to automatically inject organization_id into entities before saving.
 *
 * Usage in services:
 * <pre>
 * @Autowired
 * private EntityOrganizationHelper organizationHelper;
 *
 * public Client createClient(Client client) {
 *     organizationHelper.setOrganizationId(client);  // Auto-inject org ID
 *     return clientRepository.save(client);
 * }
 * </pre>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EntityOrganizationHelper {

    private final jakarta.persistence.EntityManager entityManager;

    /**
     * Sets the organization on an entity from the current context.
     * Throws exception if organization context is not set.
     *
     * @param entity the entity extending OrganizationScoped
     * @param <T> entity type
     * @return the entity with organization set
     */
    public <T extends OrganizationScoped> T setOrganization(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        UUID organizationId = OrganizationContext.getCurrentOrganizationId();
        Organization organization = entityManager.getReference(Organization.class, organizationId);
        entity.setOrganization(organization);

        log.debug("Organization set on entity: {} - orgId={}",
            entity.getClass().getSimpleName(), organizationId);

        return entity;
    }

    /**
     * Sets organization on an entity, or uses a specific organization ID if provided.
     * Useful for admin operations or bulk imports.
     *
     * @param entity the entity
     * @param organizationId specific organization ID (null to use context)
     * @param <T> entity type
     * @return the entity with organization set
     */
    public <T extends OrganizationScoped> T setOrganization(T entity, UUID organizationId) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        UUID orgId = organizationId != null ? organizationId : OrganizationContext.getCurrentOrganizationId();
        Organization organization = entityManager.getReference(Organization.class, orgId);
        entity.setOrganization(organization);

        log.debug("Organization set on entity: {} - orgId={}",
            entity.getClass().getSimpleName(), orgId);

        return entity;
    }

    /**
     * @deprecated Use {@link #setOrganization(OrganizationScoped)}
     */
    @Deprecated
    public <T extends OrganizationScoped> T setOrganizationId(T entity) {
        return setOrganization(entity);
    }

    /**
     * @deprecated Use {@link #setOrganization(OrganizationScoped, UUID)}
     */
    @Deprecated
    public <T extends OrganizationScoped> T setOrganizationId(T entity, UUID organizationId) {
        return setOrganization(entity, organizationId);
    }

    /**
     * Validates that an entity belongs to the current organization.
     * Useful for update/delete operations to prevent cross-organization data manipulation.
     *
     * @param entity the entity to validate
     * @param <T> entity type
     * @throws IllegalStateException if organization IDs don't match
     */
    public <T extends OrganizationScoped> void validateOrganizationMatch(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        UUID currentOrgId = OrganizationContext.getCurrentOrganizationId();
        UUID entityOrgId = entity.getOrganizationId();

        if (entityOrgId == null) {
            throw new IllegalStateException("Entity does not have organization ID set");
        }

        if (!currentOrgId.equals(entityOrgId)) {
            log.error("Organization mismatch: current={}, entity={}, entityType={}",
                currentOrgId, entityOrgId, entity.getClass().getSimpleName());
            throw new IllegalStateException(
                "Entity belongs to different organization. Operation not allowed."
            );
        }

        log.debug("Organization match validated: orgId={}, entityType={}",
            currentOrgId, entity.getClass().getSimpleName());
    }

    /**
     * Checks if an entity belongs to the current organization without throwing exception.
     *
     * @param entity the entity to check
     * @param <T> entity type
     * @return true if entity belongs to current organization
     */
    public <T extends OrganizationScoped> boolean belongsToCurrentOrganization(T entity) {
        if (entity == null || entity.getOrganizationId() == null) {
            return false;
        }

        UUID currentOrgId = OrganizationContext.getCurrentOrganizationIdOrNull();
        if (currentOrgId == null) {
            return false;
        }

        return currentOrgId.equals(entity.getOrganizationId());
    }
}
