package com.example.account.modules.core.model.enums;

/**
 * Defines user roles within an organization.
 * Determines permission levels and access control with RBAC integration.
 *
 * Role hierarchy (from highest to lowest):
 * OWNER > ADMIN_ORG > ADMIN > ACCOUNTANT > SELLER > MANAGER > MEMBER > VIEWER
 */
public enum OrganizationRole {
    /**
     * Organization owner - full control including organization deletion.
     * Typically the creator of the organization.
     * Inherits all permissions.
     */
    OWNER("Owner", 100),

    /**
     * Organization administrator - full operational control except organization deletion.
     * Can manage users, settings, and all business operations.
     * Inherits all permissions except organization deletion.
     */
    ADMIN_ORG("Organization Admin", 90),

    /**
     * Administrator - full operational control.
     * Can manage users, settings, and all business operations.
     * Legacy role maintained for backward compatibility.
     */
    ADMIN("Administrator", 80),

    /**
     * Accountant - specialized role for financial operations.
     * Can validate invoices, manage payments, view financial reports.
     * Cannot create/edit products or manage customers.
     */
    ACCOUNTANT("Accountant", 70),

    /**
     * Seller - sales operations role.
     * Can create invoices, quotes, manage customers.
     * Permissions controlled by fine-grained permission system.
     */
    SELLER("Seller", 65),

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
     * Useful for auditors or external consultants.
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
