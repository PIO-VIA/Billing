package com.example.account.aspect;

import com.example.account.annotation.RequireOrganizationAccess;
import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.repository.UserOrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * AOP Aspect that enforces organization-based access control.
 * Adapted for reactive flows.
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

        Object result = joinPoint.proceed();

        if (result instanceof Mono) {
            return Mono.deferContextual(ctx -> {
                if (!ctx.hasKey(ReactiveOrganizationContext.ORGANIZATION_ID_KEY)) {
                    return Mono.error(new IllegalStateException("Contexte d'organisation requis mais non trouvé"));
                }
                return (Mono<?>) result;
            });
        } else if (result instanceof Flux) {
            return Flux.deferContextual(ctx -> {
                if (!ctx.hasKey(ReactiveOrganizationContext.ORGANIZATION_ID_KEY)) {
                    return Flux.error(new IllegalStateException("Contexte d'organisation requis mais non trouvé"));
                }
                return (Flux<?>) result;
            });
        }

        return result;
    }
}
