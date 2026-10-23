package com.osinergmin.reporte.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Igual que en osi-back: el usuario elige días en hora de Perú y las columnas TIMESTAMP
 * guardan instantes en UTC.
 */
public final class FechasLab {

    public static final ZoneId ZONA = ZoneId.of("America/Lima");

    private FechasLab() {
    }

    /** Primer instante del día en Perú, como TIMESTAMP UTC. */
    public static LocalDateTime inicioDelDiaUtc(LocalDate dia) {
        return utc(dia.atStartOfDay(ZONA).toInstant());
    }

    /** Primer instante del día siguiente (límite exclusivo para "hasta"), como TIMESTAMP UTC. */
    public static LocalDateTime finDelDiaUtc(LocalDate dia) {
        return utc(dia.plusDays(1).atStartOfDay(ZONA).toInstant());
    }

    /** TIMESTAMP leído de la BD (UTC) como instante. */
    public static Instant desdeUtc(LocalDateTime utc) {
        return utc.toInstant(ZoneOffset.UTC);
    }

    private static LocalDateTime utc(Instant instante) {
        return LocalDateTime.ofInstant(instante, ZoneOffset.UTC);
    }
}
