package com.sitio1;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[FILTRO] LoggingFilter inicializado en Sitio1");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Filtro de logging
        System.out.println("[LOG] " + new Date() + " - Acceso a: " +
                httpRequest.getRequestURI() +
                " desde IP: " + request.getRemoteAddr());

        chain.doFilter(request, response);

        System.out.println("[LOG] Petición procesada exitosamente");
    }

    @Override
    public void destroy() {
        System.out.println("[FILTRO] LoggingFilter destruido");
    }
}