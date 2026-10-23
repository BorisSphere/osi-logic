package com.osinergmin.reporte.exception;

import com.osinergmin.reporte.dto.ErrorResponse;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ValidacionException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), Collections.singletonMap(ex.getCampo(), ex.getMessage()));
    }

    /** Fecha con formato incorrecto, p. ej. ?desde=23/10/2026 en lugar de 2026-10-23. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipo(MethodArgumentTypeMismatchException ex) {
        String mensaje = "Valor inválido para '" + ex.getName() + "' (use el formato yyyy-MM-dd)";
        return build(HttpStatus.BAD_REQUEST, mensaje, Collections.singletonMap(ex.getName(), mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Error no controlado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intente nuevamente.", null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, String> campos) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, campos));
    }
}
