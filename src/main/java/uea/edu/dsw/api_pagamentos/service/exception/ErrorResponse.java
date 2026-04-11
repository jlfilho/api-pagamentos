package uea.edu.dsw.api_pagamentos.service.exception;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    private final Instant timestamp;
    private final String path;
    private final Map<String, String> details;

    public ErrorResponse(int status, String code, String message, Instant timestamp, String path) {
        this(status, code, message, timestamp, path, null);
    }

    public ErrorResponse(int status, String code, String message, Instant timestamp, String path,
            Map<String, String> details) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
        this.details = details;
    }
}
