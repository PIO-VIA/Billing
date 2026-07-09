package com.example.account.modules.core.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final ErrorCode code;
    private final Map<String, Object> details;

    public BusinessValidationException(String message, ErrorCode code) {
        this(message, code, null);
    }

    public BusinessValidationException(String message, ErrorCode code, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}
