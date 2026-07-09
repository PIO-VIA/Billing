package com.example.account.modules.core.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BusinessConflictException extends RuntimeException {

    private final ErrorCode code;
    private final Map<String, Object> details;

    public BusinessConflictException(String message, ErrorCode code) {
        this(message, code, null);
    }

    public BusinessConflictException(String message, ErrorCode code, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}
