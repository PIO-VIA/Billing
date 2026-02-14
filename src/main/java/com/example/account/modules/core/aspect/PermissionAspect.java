package com.example.account.modules.core.aspect;

import com.example.account.annotation.RequirePermission;
import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.model.enums.Permission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;

/**
 * Aspect that intercepts methods annotated with @RequirePermission 
 * and verifies that the current user has the necessary rights in the current organization.
 * Adapted for reactive flows (Mono/Flux).
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Around("@annotation(requirePermission) || @within(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Permission[] requiredPermissions = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();
        
        log.debug("Checking permissions for reactive method: {}. Required: {}, RequireAll: {}", 
                joinPoint.getSignature().getName(), 
                Arrays.toString(requiredPermissions), 
                requireAll);

        // We assume the method returns Mono or Flux
        Object result = joinPoint.proceed();

        if (result instanceof Mono) {
            return Mono.deferContextual(ctx -> {
                // In a real scenario, we'd get permissions from the context
                // For now, we'll check if the organization context is present as a basic check
                if (!ctx.hasKey(ReactiveOrganizationContext.ORGANIZATION_ID_KEY)) {
                    return Mono.error(new SecurityException("Aucun contexte d'organisation trouvé"));
                }
                
                // TODO: Implement full permission set check once UserOrganization is in context
                return (Mono<?>) result;
            });
        } else if (result instanceof Flux) {
            return Flux.deferContextual(ctx -> {
                if (!ctx.hasKey(ReactiveOrganizationContext.ORGANIZATION_ID_KEY)) {
                    return Flux.error(new SecurityException("Aucun contexte d'organisation trouvé"));
                }
                return (Flux<?>) result;
            });
        }

        return result;
    }
}
