package com.osinergmin.reporte.exception;

/** Filtro inválido: responde 400 indicando el campo. */
public class ValidacionException extends RuntimeException {

    private final String campo;

    public ValidacionException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
