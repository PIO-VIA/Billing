package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configuration for organization-based multi-tenancy.
 *
 * Enables:
 * - AOP for @RequireOrganizationAccess annotation
 * - Organization context management
 * - Hibernate filters for automatic query filtering
 */
@Configuration
@EnableAspectJAutoProxy
public class OrganizationConfig {
    // Configuration class to enable AOP aspects for authorization guards
}
