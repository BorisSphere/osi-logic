package com.osinergmin.reporte.dto;

import java.time.Instant;

/** Evento del historial. Mismo JSON que GET /api/auditoria de osi-back. */
public class EventoAuditoria {

    private final long id;
    private final Instant fecha;
    private final String usuario;
    private final String operacion;
    private final String requerimientoCodigo;
    private final String detalle;

    public EventoAuditoria(long id, Instant fecha, String usuario, String operacion, String requerimientoCodigo,
            String detalle) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.operacion = operacion;
        this.requerimientoCodigo = requerimientoCodigo;
        this.detalle = detalle;
    }

    public long getId() {
        return id;
    }

    public Instant getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getOperacion() {
        return operacion;
    }

    public String getRequerimientoCodigo() {
        return requerimientoCodigo;
    }

    /** En un RECHAZO es el motivo. */
    public String getDetalle() {
        return detalle;
    }
}
