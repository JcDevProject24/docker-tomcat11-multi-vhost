package com.sitio2;

import java.util.Date;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class SessionListener implements ServletContextListener, HttpSessionListener {

    private static int totalSesiones = 0;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Listener para eventos de aplicación
        System.out.println("[LISTENER] Aplicación Sitio2 iniciada - " + new Date());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[LISTENER] Aplicación Sitio2 finalizada - " + new Date());
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        totalSesiones++;
        System.out.println("[SESSION] Nueva sesión en Sitio2. Total: " + totalSesiones);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("[SESSION] Sesión destruida en Sitio2");
    }
}