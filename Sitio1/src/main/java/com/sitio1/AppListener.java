package com.sitio1;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class AppListener implements ServletContextListener, HttpSessionListener {

    private static int sesionesActivas = 0;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Listener para eventos de aplicación
        System.out.println("[LISTENER] Aplicación Sitio1 iniciada");
        sce.getServletContext().setAttribute("appStartTime", System.currentTimeMillis());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[LISTENER] Aplicación Sitio1 detenida");
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sesionesActivas++;
        System.out.println("[LISTENER] Nueva sesión creada. Total: " + sesionesActivas);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sesionesActivas--;
        System.out.println("[LISTENER] Sesión destruida. Total: " + sesionesActivas);
    }
}