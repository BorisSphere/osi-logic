package com.osinergmin.reporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

/**
 * Punto de entrada en WebLogic (como war) y en local (main con Tomcat).
 * WebLogic exige que la clase implemente WebApplicationInitializer de forma directa,
 * aunque SpringBootServletInitializer ya lo haga; sin esto no arranca el contexto de Spring.
 */
@SpringBootApplication
public class OsiReporteApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OsiReporteApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(OsiReporteApplication.class, args);
    }
}
