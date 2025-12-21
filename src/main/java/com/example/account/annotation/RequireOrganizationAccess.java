package com.example.account.annotation;

import com.example.account.model.enums.OrganizationRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enforce organization-level access control on methods.
 *
 * Usage examples:
 *
 * // Require any organization access
 * @RequireOrganizationAccess
 * public void someMethod() { ... }
 *
 * // Require specific role
 * @RequireOrganizationAccess(minimumRole = OrganizationRole.ADMIN)
 * public void adminMethod() { ... }
 *
 * // Allow only organization owners
 * @RequireOrganizationAccess(minimumRole = OrganizationRole.OWNER)
 * public void ownerMethod() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireOrganizationAccess {

    /**
     * Minimum role required to access this method.
     * Default is MEMBER (any active organization member can access).
     */
    OrganizationRole minimumRole() default OrganizationRole.MEMBER;

    /**
     * Custom error message when access is denied.
     */
    String message() default "Insufficient permissions for this organization operation";
}
