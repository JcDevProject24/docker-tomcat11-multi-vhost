package com.sitio2;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[FILTRO AUTH] AuthFilter inicializado en Sitio2");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String uri = httpRequest.getRequestURI();
        String ip = request.getRemoteAddr();
        String method = httpRequest.getMethod();

        // Filtro de autenticación y logging
        System.out.println("[AUTH] " + new Date() + " | " + method + " | " + uri + " | IP: " + ip);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("[FILTRO AUTH] AuthFilter destruido");
    }
}