package com.example.account.modules.core.filter;

import com.example.account.modules.core.context.ReactiveOrganizationContext;
import com.example.account.modules.core.exception.ApiErrorResponse;
import com.example.account.modules.core.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Component
@Order(-100)
@Slf4j
@RequiredArgsConstructor
public class OrganizationFilter implements WebFilter {

    public static final String ORGANIZATION_ID_HEADER = "X-Organization-ID";

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String orgIdHeader = exchange.getRequest().getHeaders().getFirst(ORGANIZATION_ID_HEADER);
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (requiresOrganizationHeader(path) && (orgIdHeader == null || orgIdHeader.isBlank())) {
            return writeError(exchange, HttpStatus.BAD_REQUEST,
                    "L'en-tête X-Organization-ID est obligatoire",
                    ErrorCode.ORGANIZATION_HEADER_REQUIRED.name());
        }

        if (orgIdHeader != null && !orgIdHeader.isBlank()) {
            try {
                UUID.fromString(orgIdHeader);
            } catch (IllegalArgumentException e) {
                return writeError(exchange, HttpStatus.BAD_REQUEST,
                        "Format invalide pour X-Organization-ID",
                        ErrorCode.VALIDATION_ERROR.name());
            }
        }

        return chain.filter(exchange)
                .contextWrite(ctx -> {
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        ctx = ctx.put(ReactiveOrganizationContext.TOKEN_KEY, authHeader.substring(7));
                    }
                    if (orgIdHeader != null && !orgIdHeader.isBlank()) {
                        UUID orgId = UUID.fromString(orgIdHeader);
                        log.debug("Found Organization ID in header: {}", orgId);
                        ctx = ctx.put(ReactiveOrganizationContext.ORGANIZATION_ID_KEY, orgId);
                    }
                    return ctx;
                });
    }

    private boolean requiresOrganizationHeader(String path) {
        return path.startsWith("/api/")
                && !path.equals("/api/health")
                && !path.startsWith("/api/health/");
    }

    private Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus status,
            String message,
            String code) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .message(message)
                .code(code)
                .details(Map.of("path", exchange.getRequest().getPath().value()))
                .build();
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"message\":\"" + message + "\",\"code\":\"" + code + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
