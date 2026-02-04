package com.example.account.aspect;
/*
import com.example.account.annotation.RequireOrganizationAccess;
import com.example.account.modules.core.context.OrganizationContext;
import com.example.account.modules.core.model.entity.UserOrganization;
import com.example.account.modules.core.model.enums.OrganizationRole;
import com.example.account.modules.core.repository.UserOrganizationRepository;
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

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OrganizationAccessAspect {

    private final UserOrganizationRepository userOrganizationRepository;

    @Around("@annotation(com.example.account.annotation.RequireOrganizationAccess) || " +
            "@within(com.example.account.annotation.RequireOrganizationAccess)")
    public Object checkOrganizationAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        // Implementation commented out for reactive migration
        return joinPoint.proceed();
    }
}
*/
