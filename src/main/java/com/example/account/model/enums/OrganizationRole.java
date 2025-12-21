package com.example.account.model.enums;

/**
 * Defines user roles within an organization.
 * Determines permission levels and access control (future RBAC integration).
 *
 * Role hierarchy (from highest to lowest):
 * OWNER > ADMIN > MANAGER > MEMBER > VIEWER
 */
public enum OrganizationRole {
    /**
     * Organization owner - full control including organization deletion.
     * Typically the creator of the organization.
     */
    OWNER("Owner", 100),

    /**
     * Administrator - full operational control except organization deletion.
     * Can manage users, settings, and all business operations.
     */
    ADMIN("Administrator", 80),

    /**
     * Manager - can manage business operations within the organization.
     * Cannot modify organization settings or user permissions.
     */
    MANAGER("Manager", 60),

    /**
     * Standard member - can perform standard business operations.
     * Read/write access to business data within assigned scope.
     */
    MEMBER("Member", 40),

    /**
     * Viewer - read-only access to organization data.
     * Useful for accountants, auditors, or external consultants.
     */
    VIEWER("Viewer", 20);

    private final String displayName;
    private final int level;

    OrganizationRole(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Checks if this role has at least the same level as the given role.
     */
    public boolean hasAtLeastLevel(OrganizationRole requiredRole) {
        return this.level >= requiredRole.level;
    }

    /**
     * Checks if this role is higher than the given role.
     */
    public boolean isHigherThan(OrganizationRole otherRole) {
        return this.level > otherRole.level;
    }
}
