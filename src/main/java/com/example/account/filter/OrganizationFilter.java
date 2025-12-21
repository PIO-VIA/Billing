package com.example.account.filter;

import com.example.account.context.OrganizationContext;
import com.example.account.repository.UserOrganizationRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Servlet filter that extracts and validates organization context from HTTP requests.
 *
 * Organization resolution order:
 * 1. X-Organization-ID header (primary method)
 * 2. JWT claim 'org_id' (future implementation)
 * 3. User's default organization (fallback)
 *
 * This filter:
 * - Extracts organization ID from request
 * - Validates user has access to the organization
 * - Sets OrganizationContext for the request thread
 * - Enables Hibernate organization filter
 * - Clears context after request completes
 */
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class OrganizationFilter implements Filter {

    private final EntityManager entityManager;
    private final UserOrganizationRepository userOrganizationRepository;

    /**
     * Endpoints that don't require organization context.
     * Typically: auth endpoints, health checks, public APIs.
     */
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/auth/",
        "/api/users/register",
        "/api/users/login",
        "/api/health",
        "/actuator/",
        "/swagger-ui/",
        "/v3/api-docs/",
        "/api/organizations/create"  // Organization creation doesn't require existing org context
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getRequestURI();

        // Skip filter for excluded paths
        if (isExcludedPath(requestPath)) {
            log.debug("Skipping organization filter for path: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        try {
            // Extract organization ID from header
            String organizationIdHeader = httpRequest.getHeader("X-Organization-ID");

            if (organizationIdHeader == null || organizationIdHeader.trim().isEmpty()) {
                log.warn("Missing X-Organization-ID header for path: {}", requestPath);
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\": \"Missing X-Organization-ID header\", " +
                    "\"message\": \"Organization context is required for this operation\"}"
                );
                return;
            }

            UUID organizationId;
            try {
                organizationId = UUID.fromString(organizationIdHeader);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid X-Organization-ID format: {}", organizationIdHeader);
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\": \"Invalid X-Organization-ID format\", " +
                    "\"message\": \"Organization ID must be a valid UUID\"}"
                );
                return;
            }

            // TODO: Extract user ID from JWT token when authentication is implemented
            // For now, we'll skip user validation
            // UUID userId = extractUserIdFromJWT(httpRequest);
            // validateUserAccess(userId, organizationId);

            // Set organization context
            OrganizationContext.setCurrentOrganizationId(organizationId);

            // Enable Hibernate organization filter
            Session session = entityManager.unwrap(Session.class);
            org.hibernate.Filter filter = session.enableFilter("organizationFilter");
            filter.setParameter("organizationId", organizationId);

            log.debug("Organization context activated: {} for path: {}", organizationId, requestPath);

            // Continue with the request
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error in OrganizationFilter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"error\": \"Organization context error\", " +
                "\"message\": \"" + e.getMessage() + "\"}"
            );
        } finally {
            // Always clear context to prevent memory leaks
            OrganizationContext.clear();

            // Disable Hibernate filter
            try {
                Session session = entityManager.unwrap(Session.class);
                session.disableFilter("organizationFilter");
            } catch (Exception e) {
                log.warn("Failed to disable Hibernate filter", e);
            }
        }
    }

    /**
     * Checks if the request path should be excluded from organization filtering.
     */
    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    /**
     * Validates that the user has access to the specified organization.
     * TODO: Uncomment when authentication is implemented.
     */
    /*
    private void validateUserAccess(UUID userId, UUID organizationId) {
        boolean hasAccess = userOrganizationRepository.existsActiveByUserIdAndOrganizationId(
            userId, organizationId
        );

        if (!hasAccess) {
            throw new AccessDeniedException(
                "User does not have access to organization: " + organizationId
            );
        }
    }
    */

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("OrganizationFilter initialized");
    }

    @Override
    public void destroy() {
        log.info("OrganizationFilter destroyed");
    }
}
