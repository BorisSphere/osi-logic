package com.osinergmin.reporte.repository;

import com.osinergmin.reporte.dto.EventoAuditoria;
import com.osinergmin.reporte.util.FechasLab;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/** Lectura de LAB_AUDITORIA (solo consulta; los eventos los escribe osi-back). */
@Repository
public class AuditoriaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public AuditoriaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Historial global, del más reciente al más antiguo. null = sin filtro; hasta es exclusivo. */
    public List<EventoAuditoria> buscar(LocalDateTime desdeUtc, LocalDateTime hastaUtc, String usuario) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.ID, a.FECHA, u.USERNAME, a.ACCION, a.REQUERIMIENTO_ID, a.DETALLE"
                        + " FROM LAB_AUDITORIA a JOIN LAB_USUARIO u ON u.ID = a.ACTOR_ID WHERE 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (desdeUtc != null) {
            sql.append(" AND a.FECHA >= :desde");
            params.addValue("desde", desdeUtc);
        }
        if (hastaUtc != null) {
            sql.append(" AND a.FECHA < :hasta");
            params.addValue("hasta", hastaUtc);
        }
        if (usuario != null) {
            sql.append(" AND LOWER(u.USERNAME) = LOWER(:usuario)");
            params.addValue("usuario", usuario);
        }
        sql.append(" ORDER BY a.FECHA DESC, a.ID DESC");

        return jdbc.query(sql.toString(), params, (rs, i) -> new EventoAuditoria(
                rs.getLong("ID"),
                FechasLab.desdeUtc(rs.getObject("FECHA", LocalDateTime.class)),
                rs.getString("USERNAME"),
                rs.getString("ACCION"),
                // Mismo formato que CodigoRequerimiento de osi-back
                String.format("REQ-%04d", rs.getLong("REQUERIMIENTO_ID")),
                rs.getString("DETALLE")));
    }

    /** Usuarios que tienen al menos una operación registrada (para el filtro). */
    public List<String> usuariosConOperaciones() {
        return jdbc.getJdbcTemplate().queryForList(
                "SELECT DISTINCT u.USERNAME FROM LAB_AUDITORIA a JOIN LAB_USUARIO u ON u.ID = a.ACTOR_ID"
                        + " ORDER BY u.USERNAME",
                String.class);
    }
}
