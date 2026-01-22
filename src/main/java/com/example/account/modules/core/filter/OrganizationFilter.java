package com.example.account.modules.core.filter;

import com.example.account.modules.core.context.OrganizationContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter responsible for extracting the Organization ID from the request header 
 * and setting it in the ThreadLocal OrganizationContext.
 */
@Component
@Slf4j
public class OrganizationFilter implements Filter {

    public static final String ORGANIZATION_ID_HEADER = "X-Organization-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String orgIdHeader = httpRequest.getHeader(ORGANIZATION_ID_HEADER);

            if (orgIdHeader != null && !orgIdHeader.isBlank()) {
                try {
                    UUID orgId = UUID.fromString(orgIdHeader);
                    OrganizationContext.setCurrentOrganizationId(orgId);
                    log.debug("Found Organization ID in header: {}", orgId);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid Organization ID format in header: {}", orgIdHeader);
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // CRITICAL: Always clear context to prevent memory leaks and cross-request contamination
            OrganizationContext.clear();
        }
    }
}
