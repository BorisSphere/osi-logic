package com.osinergmin.reporte.controller;

import com.osinergmin.reporte.dto.EventoAuditoria;
import com.osinergmin.reporte.service.AuditoriaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Historial de operaciones (sin autenticación). */
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /** Filtros opcionales: ?desde=2026-10-01&hasta=2026-10-31&usuario=aprobador1 */
    @GetMapping
    public List<EventoAuditoria> buscar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String usuario) {
        return auditoriaService.buscar(desde, hasta, usuario);
    }

    /** Usuarios para el filtro de la pantalla. */
    @GetMapping("/usuarios")
    public List<String> usuarios() {
        return auditoriaService.usuarios();
    }
}
