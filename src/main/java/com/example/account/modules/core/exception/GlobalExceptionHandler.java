package com.example.account.modules.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessConflictException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleConflict(
            BusinessConflictException ex, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), ex.getCode().name(), ex.getDetails(), exchange)));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleValidation(
            BusinessValidationException ex, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(ex.getMessage(), ex.getCode().name(), ex.getDetails(), exchange)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleIllegalArgument(
            IllegalArgumentException ex, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(ex.getMessage(), ErrorCode.VALIDATION_ERROR.name(), null, exchange)));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleBindException(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(message, ErrorCode.VALIDATION_ERROR.name(), null, exchange)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleResponseStatus(
            ResponseStatusException ex, ServerWebExchange exchange) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String code = status.is4xxClientError()
                ? ErrorCode.VALIDATION_ERROR.name()
                : ErrorCode.INTERNAL_ERROR.name();
        return Mono.just(ResponseEntity.status(status)
                .body(buildError(ex.getReason(), code, null, exchange)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleGlobalException(
            Exception ex, ServerWebExchange exchange) {
        ex.printStackTrace();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        ex.getMessage() != null ? ex.getMessage() : "Internal Server Error",
                        ErrorCode.INTERNAL_ERROR.name(),
                        null,
                        exchange)));
    }

    private ApiErrorResponse buildError(
            String message,
            String code,
            Map<String, Object> details,
            ServerWebExchange exchange) {
        Map<String, Object> enrichedDetails = details != null ? new HashMap<>(details) : new HashMap<>();
        enrichedDetails.putIfAbsent("path", exchange.getRequest().getPath().value());
        return ApiErrorResponse.builder()
                .message(message)
                .code(code)
                .details(enrichedDetails)
                .build();
    }
}
