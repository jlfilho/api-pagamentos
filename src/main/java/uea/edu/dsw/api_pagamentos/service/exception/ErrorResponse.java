package uea.edu.dsw.api_pagamentos.service.exception;

import java.time.Instant;

import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String error;
    private Instant timestamp;

    public ErrorResponse(int status, String error, Instant timestamp) {
        this.status = status;
        this.error = error;
        this.timestamp = timestamp;
    }
}

