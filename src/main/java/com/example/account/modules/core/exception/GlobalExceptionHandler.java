package com.example.account.modules.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Object>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        log.error("Invalid argument at {}: {}", exchange.getRequest().getPath(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 2. IllegalStateException
    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<Object>> handleIllegalStateException(IllegalStateException ex, ServerWebExchange exchange) {
        log.error("Illegal state at {}: {}", exchange.getRequest().getPath(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // 3. Validation Exceptions (In WebFlux, MethodArgumentNotValidException is replaced by WebExchangeBindException)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Object>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        org.springframework.validation.FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error"
                ));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");
        body.put("message", "Validation failed");
        body.put("details", errors);

        log.error("Validation error: {}", errors);

        return Mono.just(new ResponseEntity<>(body, HttpStatus.BAD_REQUEST));
    }

    // 4. Fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Object>> handleGlobalException(Exception ex, ServerWebExchange exchange) {
        log.error("Internal Server Error at {}: ", exchange.getRequest().getPath(), ex);
        return buildErrorResponse("Une erreur interne est survenue. Veuillez contacter le support.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method returning Mono
    private Mono<ResponseEntity<Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return Mono.just(new ResponseEntity<>(body, status));
    }
}