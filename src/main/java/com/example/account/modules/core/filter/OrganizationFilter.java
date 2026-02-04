package com.example.account.modules.core.filter;

import com.example.account.modules.core.context.OrganizationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * WebFilter responsible for extracting the Organization ID from the request header 
 * and setting it in the Reactor Context.
 */
@Component
@Slf4j
public class OrganizationFilter implements WebFilter {

    public static final String ORGANIZATION_ID_HEADER = "X-Organization-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String orgIdHeader = exchange.getRequest().getHeaders().getFirst(ORGANIZATION_ID_HEADER);
        UUID orgId = null;

        if (orgIdHeader != null && !orgIdHeader.isBlank()) {
            try {
                orgId = UUID.fromString(orgIdHeader);
                log.debug("Found Organization ID in header: {}", orgId);
            } catch (IllegalArgumentException e) {
                log.error("Invalid Organization ID format in header: {}", orgIdHeader);
            }
        }

        if (orgId != null) {
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put(OrganizationContext.KEY_ORGANIZATION_ID, orgId));
        }

        return chain.filter(exchange);
    }
}
