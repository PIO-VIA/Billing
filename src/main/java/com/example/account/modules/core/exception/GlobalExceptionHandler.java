package com.example.account.modules.core.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange; // Use this instead of HttpServletRequest
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ResponseStatusException (and subtypes like WebExchangeBindException from @Valid
     * failures) already carry the real status — SalesCoreErrorMapper in particular
     * builds these deliberately to preserve sales-core's status code. Without this
     * handler, the catch-all Exception handler below discarded it and always returned
     * 500, turning e.g. a real 404 or a validation 400 into an opaque server error.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Object>> handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getReason());
        body.put("path", exchange.getRequest().getPath().value());
        body.put("error", ex.getStatusCode().toString());

        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(body));
    }

    /**
     * Kernel-calling adapters that use .retrieve() without an explicit
     * .onStatus() handler (most of them — SalesCoreErrorMapper is opt-in, not
     * automatic) throw this raw exception type instead of ResponseStatusException
     * when Kernel itself returns a 4xx/5xx. Most importantly: an expired Kernel
     * token (they only last 15 minutes) surfaces as a 401 here — without this
     * handler that fell into the catch-all below and always came back as an
     * opaque 500, which the frontend can't distinguish from a real server error
     * to redirect the user to log in again.
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<Object>> handleWebClientResponseException(WebClientResponseException ex, ServerWebExchange exchange) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", SalesCoreErrorMapper.extractMessage(ex.getResponseBodyAsString()));
        body.put("path", exchange.getRequest().getPath().value());
        body.put("error", ex.getStatusCode().toString());

        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Object>> handleGlobalException(Exception ex, ServerWebExchange exchange) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("path", exchange.getRequest().getPath().value());
        body.put("error", "Internal Server Error");

        // Log the error for debugging
        ex.printStackTrace(); 

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}