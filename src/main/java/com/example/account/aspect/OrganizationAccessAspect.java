package com.example.account.aspect;

import com.example.account.annotation.RequireOrganizationAccess;
import com.example.account.context.OrganizationContext;
import com.example.account.model.entity.UserOrganization;
import com.example.account.model.enums.OrganizationRole;
import com.example.account.repository.UserOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

/**
 * AOP Aspect that enforces organization-based access control.
 *
 * Intercepts methods annotated with @RequireOrganizationAccess and validates:
 * 1. Organization context is set
 * 2. User has active membership in the organization
 * 3. User has sufficient role/permissions
 *
 * TODO: When authentication is implemented, uncomment user validation logic.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OrganizationAccessAspect {

    private final UserOrganizationRepository userOrganizationRepository;

    @Around("@annotation(com.example.account.annotation.RequireOrganizationAccess) || " +
            "@within(com.example.account.annotation.RequireOrganizationAccess)")
    public Object checkOrganizationAccess(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Get annotation (check method first, then class)
        RequireOrganizationAccess annotation = method.getAnnotation(RequireOrganizationAccess.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(RequireOrganizationAccess.class);
        }

        if (annotation == null) {
            // Should not happen, but safety check
            log.warn("@RequireOrganizationAccess annotation not found on method: {}", method.getName());
            return joinPoint.proceed();
        }

        // Validate organization context is set
        if (!OrganizationContext.hasOrganizationContext()) {
            log.error("Organization context not set for method: {}", method.getName());
            throw new IllegalStateException("Organization context is required but not set");
        }

        UUID organizationId = OrganizationContext.getCurrentOrganizationId();
        log.debug("Validating organization access: orgId={}, method={}, requiredRole={}",
            organizationId, method.getName(), annotation.minimumRole());

        // TODO: Uncomment when authentication is implemented
        /*
        UUID userId = OrganizationContext.getCurrentUserId();
        if (userId == null) {
            throw new AccessDeniedException("User context is required but not set");
        }

        // Validate user has access and sufficient role
        validateUserRole(userId, organizationId, annotation.minimumRole(), annotation.message());
        */

        // Log access for audit trail
        log.info("Organization access granted: orgId={}, method={}", organizationId, method.getName());

        // Proceed with method execution
        return joinPoint.proceed();
    }

    /**
     * Validates user has the required role in the organization.
     * TODO: Uncomment when authentication is implemented.
     */
    /*
    private void validateUserRole(UUID userId, UUID organizationId, OrganizationRole requiredRole, String message)
            throws AccessDeniedException {

        UserOrganization membership = userOrganizationRepository
            .findActiveByUserIdAndOrganizationId(userId, organizationId)
            .orElseThrow(() -> new AccessDeniedException(
                "User does not have active membership in organization: " + organizationId
            ));

        if (!membership.getRole().hasAtLeastLevel(requiredRole)) {
            log.warn("Insufficient role: userId={}, orgId={}, currentRole={}, requiredRole={}",
                userId, organizationId, membership.getRole(), requiredRole);
            throw new AccessDeniedException(message);
        }

        log.debug("User role validated: userId={}, orgId={}, role={}", userId, organizationId, membership.getRole());
    }
    */
}
