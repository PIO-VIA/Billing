package com.example.account.aspect;

import com.example.account.annotation.RequirePermission;
import com.example.account.context.OrganizationContext;
import com.example.account.model.enums.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AOP Aspect for enforcing @RequirePermission annotation.
 * Checks if the current user has the required permissions in the current organization.
 * Throws PermissionDeniedException if the user lacks required permissions.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final OrganizationContext organizationContext;

    /**
     * Advice that runs before any method annotated with @RequirePermission.
     * Validates that the current user has the necessary permissions.
     *
     * @param joinPoint Join point for the intercepted method
     * @param requirePermission The permission annotation
     * @throws AccessDeniedException if user lacks required permissions
     */
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Permission[] requiredPermissions = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();
        
        if (requiredPermissions.length == 0) {
            log.warn("@RequirePermission annotation used without specifying any permissions on method: {}", 
                    joinPoint.getSignature().getName());
            return;
        }

        // Get current user's permissions in the current organization
        Set<Permission> userPermissions = organizationContext.getCurrentUserPermissions();
        
        log.debug("Checking permissions for method: {}. Required: {}, User has: {}", 
                joinPoint.getSignature().getName(),
                Arrays.toString(requiredPermissions),
                userPermissions);

        boolean hasPermission;
        if (requireAll) {
            // User must have ALL specified permissions (AND logic)
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            // User must have AT LEAST ONE permission (OR logic)
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            String permissionsStr = Arrays.stream(requiredPermissions)
                    .map(Permission::getDisplayName)
                    .collect(Collectors.joining(requireAll ? " AND " : " OR "));
            
            String errorMessage = requirePermission.message().isEmpty()
                    ? String.format("Access denied. Required permission%s: %s",
                            requireAll ? "s" : "",
                            permissionsStr)
                    : requirePermission.message();
            
            log.warn("Permission check failed for user {} in organization {}. Method: {}. Required: {}",
                    organizationContext.getCurrentUserId(),
                    organizationContext.getCurrentOrganizationId(),
                    joinPoint.getSignature().getName(),
                    permissionsStr);
            
            throw new RuntimeException("Access denied. " + errorMessage);
        }

        log.debug("Permission check passed for method: {}", joinPoint.getSignature().getName());
    }
}
