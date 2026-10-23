package com.osinergmin.reporte.service;

import com.osinergmin.reporte.dto.EventoAuditoria;
import com.osinergmin.reporte.exception.ValidacionException;
import com.osinergmin.reporte.repository.AuditoriaRepository;
import com.osinergmin.reporte.util.FechasLab;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository repository;

    public AuditoriaService(AuditoriaRepository repository) {
        this.repository = repository;
    }

    /** Historial global. desde y hasta son días completos e inclusivos; cualquiera puede omitirse. */
    public List<EventoAuditoria> buscar(LocalDate desde, LocalDate hasta, String usuario) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ValidacionException("desde", "La fecha \"desde\" no puede ser posterior a \"hasta\"");
        }
        LocalDateTime inicio = desde == null ? null : FechasLab.inicioDelDiaUtc(desde);
        LocalDateTime fin = hasta == null ? null : FechasLab.finDelDiaUtc(hasta);
        String filtroUsuario = usuario == null || usuario.isBlank() ? null : usuario.trim();
        return repository.buscar(inicio, fin, filtroUsuario);
    }

    public List<String> usuarios() {
        return repository.usuariosConOperaciones();
    }
}
