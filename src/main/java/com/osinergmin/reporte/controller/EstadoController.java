package com.osinergmin.reporte.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Verificación del despliegue: GET /osi-reporte/api/estado. */
@RestController
@RequestMapping("/api")
public class EstadoController {

    private final ServletContext servletContext;
    private final String nombre;

    public EstadoController(ServletContext servletContext, @Value("${spring.application.name}") String nombre) {
        this.servletContext = servletContext;
        this.nombre = nombre;
    }

    @GetMapping("/estado")
    public Map<String, Object> estado() {
        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("aplicacion", nombre);
        estado.put("estado", "OK");
        estado.put("servidor", servletContext.getServerInfo()); // p. ej. "WebLogic Server 14.1.1.0.0 ..."
        estado.put("contextPath", servletContext.getContextPath());
        estado.put("springBoot", SpringBootVersion.getVersion());
        estado.put("java", System.getProperty("java.version"));
        estado.put("fecha", Instant.now().toString());
        return estado;
    }
}
