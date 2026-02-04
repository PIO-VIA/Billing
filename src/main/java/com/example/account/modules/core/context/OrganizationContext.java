package com.example.account.modules.core.context;

import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.model.enums.Permission;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Storage for the current organization context, supporting both ThreadLocal (legacy/blocking)
 * and Reactor Context (reactive/non-blocking).
 */
@Slf4j
public class OrganizationContext {

    // Keys for Reactor Context
    public static final String KEY_ORGANIZATION_ID = "organizationId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_ORGANIZATION = "userOrganization";
    public static final String KEY_USER_PERMISSIONS = "userPermissions";
    public static final String KEY_USER_ROLE = "userRole";

    // Legacy ThreadLocal storage
    private static final ThreadLocal<UUID> CURRENT_ORGANIZATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<UserOrganization> CURRENT_USER_ORGANIZATION = new ThreadLocal<>();
    private static final ThreadLocal<Set<Permission>> CURRENT_USER_PERMISSIONS = new ThreadLocal<>();
    private static final ThreadLocal<OrganizationRole> CURRENT_USER_ROLE = new ThreadLocal<>();

    private OrganizationContext() {}

    // --- Reactive Context Methods ---

    /**
     * Gets the current organization ID from the Reactor Context.
     */
    public static Mono<UUID> getOrganizationId() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(KEY_ORGANIZATION_ID)) {
                return Mono.just(ctx.get(KEY_ORGANIZATION_ID));
            }
            return Mono.error(new IllegalStateException("No organization context set in Reactor Context"));
        });
    }

    /**
     * Extracts organization ID from ContextView.
     */
    public static UUID getOrganizationId(ContextView context) {
        return context.getOrDefault(KEY_ORGANIZATION_ID, null);
    }

    /**
     * Creates a Reactor Context with the given organization ID.
     */
    public static Context withOrganizationId(UUID organizationId) {
        return Context.of(KEY_ORGANIZATION_ID, organizationId);
    }

    // --- Legacy ThreadLocal Methods (Discouraged in Reactive Flow) ---

    public static void setCurrentOrganizationId(UUID organizationId) {
        if (organizationId == null) throw new IllegalArgumentException("Organization ID cannot be null");
        CURRENT_ORGANIZATION_ID.set(organizationId);
    }

    public static UUID getCurrentOrganizationId() {
        UUID organizationId = CURRENT_ORGANIZATION_ID.get();
        if (organizationId == null) {
            throw new IllegalStateException("No organization context set for current thread.");
        }
        return organizationId;
    }

    public static UUID getCurrentOrganizationIdOrNull() {
        return CURRENT_ORGANIZATION_ID.get();
    }

    public static void setCurrentUserId(UUID userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static UUID getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setCurrentUserOrganization(UserOrganization userOrganization) {
        CURRENT_USER_ORGANIZATION.set(userOrganization);
        if (userOrganization != null) {
            CURRENT_USER_PERMISSIONS.set(userOrganization.getActivePermissions());
            CURRENT_USER_ROLE.set(userOrganization.getRole());
        }
    }

    public static void clear() {
        CURRENT_ORGANIZATION_ID.remove();
        CURRENT_USER_ID.remove();
        CURRENT_USER_ORGANIZATION.remove();
        CURRENT_USER_PERMISSIONS.remove();
        CURRENT_USER_ROLE.remove();
    }
}
