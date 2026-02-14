package com.example.account.modules.core.service;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.model.entity.OrganizationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Helper class to automatically inject organization_id into entities before saving.
 * 
 * Usage in services:
 * <pre>
 * @Autowired
 * private EntityOrganizationHelper organizationHelper;
 * 
 * public Mono<Client> createClient(Client client) {
 *     return organizationHelper.setOrganizationId(client)
 *             .flatMap(clientRepository::save);
 * }
 * </pre>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EntityOrganizationHelper {

    /**
     * Sets the organization on an entity from the current reactive context.
     * 
     * @param entity the entity extending OrganizationScoped
     * @param <T> entity type
     * @return a Mono containing the entity with organization set
     */
    public <T extends OrganizationScoped> Mono<T> setOrganizationId(T entity) {
        return ReactiveOrganizationContext.getOrganizationId()
                .map(orgId -> {
                    entity.setOrganizationId(orgId);
                    log.debug("Organization ID {} injected into entity", orgId);
                    return entity;
                });
    }

    /**
     * Sets the organization on an entity from the current reactive context, or continues if not found.
     * 
     * @param entity the entity extending OrganizationScoped
     * @param <T> entity type
     * @return a Mono containing the entity
     */
    public <T extends OrganizationScoped> Mono<T> setOrganizationIdIfPresent(T entity) {
        return ReactiveOrganizationContext.getOrganizationIdOrEmpty()
                .map(orgId -> {
                    entity.setOrganizationId(orgId);
                    return entity;
                })
                .defaultIfEmpty(entity);
    }
}
