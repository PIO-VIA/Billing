package com.example.account.modules.core.aspect;

import com.example.account.annotation.RequirePermission;
import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.core.model.enums.Permission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * Aspect that intercepts methods annotated with @RequirePermission 
 * and verifies that the current user has the necessary rights in the current organization.
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Before("@annotation(requirePermission) || @within(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Permission[] requiredPermissions = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();
        
        Set<Permission> userPermissions = OrganizationContext.getCurrentUserPermissions();
        
        log.debug("Checking permissions for method: {}. Required: {}, RequireAll: {}", 
                joinPoint.getSignature().getName(), 
                Arrays.toString(requiredPermissions), 
                requireAll);

        boolean hasAccess;
        if (requireAll) {
            hasAccess = userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            hasAccess = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasAccess) {
            String message = requirePermission.message();
            if (message.isBlank()) {
                message = "Accès refusé. Permissions requises : " + Arrays.toString(requiredPermissions);
            }
            log.warn("Permission check failed for user. {}", message);
            throw new SecurityException(message);
        }
    }
}
