package com.osinergmin.reporte.dto;

import java.time.Instant;
import java.util.Map;

/** Mismo formato de error que osi-back. */
public class ErrorResponse {

    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> campos;

    public ErrorResponse(int status, String error, String message, Map<String, String> campos) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.campos = campos;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getCampos() {
        return campos;
    }
}
