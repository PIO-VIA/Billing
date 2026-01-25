package com.example.account.modules.core.context;

import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.model.enums.Permission;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Thread-local storage for the current organization context.
 * This class manages the organization scope for each request thread,
 * including permissions and role information.
 *
 * Usage:
 * - OrganizationContext.setCurrentOrganizationId(orgId);
 * - UUID orgId = OrganizationContext.getCurrentOrganizationId();
 * - OrganizationContext.clear();
 *
 * IMPORTANT: Always call clear() in a finally block to prevent memory leaks.
 */
@Slf4j
public class OrganizationContext {

    private static final ThreadLocal<UUID> CURRENT_ORGANIZATION_ID = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<UserOrganization> CURRENT_USER_ORGANIZATION = new ThreadLocal<>();
    private static final ThreadLocal<Set<Permission>> CURRENT_USER_PERMISSIONS = new ThreadLocal<>();
    private static final ThreadLocal<OrganizationRole> CURRENT_USER_ROLE = new ThreadLocal<>();

    private OrganizationContext() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sets the current organization ID for this thread.
     */
    public static void setCurrentOrganizationId(UUID organizationId) {
        if (organizationId == null) {
            log.warn("Attempting to set null organization ID in context");
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        CURRENT_ORGANIZATION_ID.set(organizationId);
        log.debug("Organization context set: {}", organizationId);
    }

    /**
     * Gets the current organization ID for this thread.
     *
     * @return the organization ID
     * @throws IllegalStateException if no organization context is set
     */
    public static UUID getCurrentOrganizationId() {
        UUID organizationId = CURRENT_ORGANIZATION_ID.get();
        if (organizationId == null) {
            throw new IllegalStateException("No organization context set for current thread. " +
                "Ensure OrganizationFilter is properly configured and X-Organization-ID header is provided.");
        }
        return organizationId;
    }

    /**
     * Gets the current organization ID without throwing exception.
     *
     * @return the organization ID or null if not set
     */
    public static UUID getCurrentOrganizationIdOrNull() {
        return CURRENT_ORGANIZATION_ID.get();
    }

    /**
     * Checks if organization context is set.
     */
    public static boolean hasOrganizationContext() {
        return CURRENT_ORGANIZATION_ID.get() != null;
    }

    /**
     * Sets the current user ID for this thread (for audit purposes).
     */
    public static void setCurrentUserId(UUID userId) {
        CURRENT_USER_ID.set(userId);
        log.debug("User context set: {}", userId);
    }

    /**
     * Gets the current user ID for this thread.
     */
    public static UUID getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * Gets the current user ID without throwing exception.
     */
    public static UUID getCurrentUserIdOrNull() {
        return CURRENT_USER_ID.get();
    }

    /**
     * Sets the current UserOrganization for this thread.
     * This includes the user's role and permissions in the organization.
     */
    public static void setCurrentUserOrganization(UserOrganization userOrganization) {
        CURRENT_USER_ORGANIZATION.set(userOrganization);
        if (userOrganization != null) {
            CURRENT_USER_PERMISSIONS.set(userOrganization.getActivePermissions());
            CURRENT_USER_ROLE.set(userOrganization.getRole());
            log.debug("UserOrganization context set: userId={}, orgId={}, role={}",
                    userOrganization.getUser().getId(),
                    userOrganization.getOrganization().getId(),
                    userOrganization.getRole());
        }
    }

    /**
     * Gets the current UserOrganization for this thread.
     */
    public static UserOrganization getCurrentUserOrganization() {
        return CURRENT_USER_ORGANIZATION.get();
    }

    /**
     * Gets the current user's permissions in the current organization.
     * Returns an empty set if no permissions are set.
     */
    public static Set<Permission> getCurrentUserPermissions() {
        Set<Permission> permissions = CURRENT_USER_PERMISSIONS.get();
        return permissions != null ? permissions : Collections.emptySet();
    }

    /**
     * Checks if the current user has a specific permission.
     */
    public static boolean hasPermission(Permission permission) {
        Set<Permission> permissions = CURRENT_USER_PERMISSIONS.get();
        return permissions != null && permissions.contains(permission);
    }

    /**
     * Gets the current user's role in the current organization.
     */
    public static OrganizationRole getCurrentUserRole() {
        return CURRENT_USER_ROLE.get();
    }

    /**
     * Checks if the current user has a specific role or higher.
     */
    public static boolean hasRole(OrganizationRole role) {
        OrganizationRole currentRole = CURRENT_USER_ROLE.get();
        return currentRole != null && currentRole.hasAtLeastLevel(role);
    }

    /**
     * Clears the organization and user context for this thread.
     * MUST be called at the end of request processing to prevent memory leaks.
     */
    public static void clear() {
        UUID orgId = CURRENT_ORGANIZATION_ID.get();
        UUID userId = CURRENT_USER_ID.get();

        CURRENT_ORGANIZATION_ID.remove();
        CURRENT_USER_ID.remove();
        CURRENT_USER_ORGANIZATION.remove();
        CURRENT_USER_PERMISSIONS.remove();
        CURRENT_USER_ROLE.remove();

        log.debug("Organization context cleared: orgId={}, userId={}", orgId, userId);
    }

    /**
     * Executes a runnable within a specific organization context.
     * Useful for background tasks or async operations.
     */
    public static void executeInContext(UUID organizationId, Runnable task) {
        UUID previousOrgId = getCurrentOrganizationIdOrNull();
        try {
            setCurrentOrganizationId(organizationId);
            task.run();
        } finally {
            clear();
            if (previousOrgId != null) {
                setCurrentOrganizationId(previousOrgId);
            }
        }
    }

    /**
     * Executes a runnable within a specific organization and user context.
     */
    public static void executeInContext(UUID organizationId, UUID userId, Runnable task) {
        UUID previousOrgId = getCurrentOrganizationIdOrNull();
        UUID previousUserId = getCurrentUserIdOrNull();
        try {
            setCurrentOrganizationId(organizationId);
            setCurrentUserId(userId);
            task.run();
        } finally {
            clear();
            if (previousOrgId != null) {
                setCurrentOrganizationId(previousOrgId);
            }
            if (previousUserId != null) {
                setCurrentUserId(previousUserId);
            }
        }
    }
}
