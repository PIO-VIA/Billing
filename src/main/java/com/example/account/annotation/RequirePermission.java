package com.example.account.annotation;

import com.example.account.model.enums.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require specific permissions for method execution.
 * Can be applied to controller methods or service methods.
 * 
 * Example usage:
 * <pre>
 * {@code
 * @RequirePermission(Permission.CREATE_INVOICE)
 * public FactureResponse createFacture(FactureCreateRequest request) {
 *     // ...
 * }
 * 
 * @RequirePermission(value = {Permission.EDIT_INVOICE, Permission.DELETE_INVOICE}, requireAll = false)
 * public void updateOrDeleteFacture(UUID id) {
 *     // User needs EITHER EDIT_INVOICE OR DELETE_INVOICE permission
 * }
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * Required permissions. User must have at least one of these permissions
     * (OR logic) unless requireAll is set to true (AND logic).
     *
     * @return Array of required permissions
     */
    Permission[] value();
    
    /**
     * If true, user must have ALL specified permissions (AND logic).
     * If false (default), user must have AT LEAST ONE permission (OR logic).
     *
     * @return Whether all permissions are required
     */
    boolean requireAll() default false;
    
    /**
     * Error message to return when permission check fails.
     * Default message is auto-generated from the permission names.
     *
     * @return Custom error message
     */
    String message() default "";
}
