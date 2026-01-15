package com.sitio2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Incluir timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        // Incluir dirección IP del contenedor
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        // Gestión de sesiones persistentes
        HttpSession session = request.getSession(true);
        Integer contador = (Integer) session.getAttribute("contador");
        if (contador == null) {
            contador = 1;
        } else {
            contador++;
        }
        session.setAttribute("contador", contador);

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Sitio 2</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; margin: 40px; background: #e8f5e9; }");
        out.println("h1 { color: #1b5e20; }");
        out.println(".info { background: white; padding: 20px; border-radius: 5px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='info'>");
        // Servlet que devuelve: "¡Hola mundo desde el Sitio 2!"
        out.println("<h1>¡Hola mundo desde el Sitio 2!</h1>");
        out.println("<p><b>Timestamp:</b> " + timestamp + "</p>");
        out.println("<p><b>Container IP:</b> " + ipAddress + "</p>");
        out.println("<p><b>Contador de accesos:</b> " + contador + "</p>");
        out.println("<p><a href='dashboard.jsp'>Ver Dashboard JSP</a></p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}