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
    /*
    
    
    */
}
