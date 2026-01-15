adasd<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html>
<head>
    <title>Info JSP - Sitio 1</title>
    <style>
        body { font-family: Arial; margin: 40px; background: #ecf0f1; }
        .container { background: white; padding: 30px; border-radius: 5px; }
        h2 { color: #27ae60; }
        table { border-collapse: collapse; width: 100%; }
        td { padding: 10px; border: 1px solid #ddd; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Información JSP - Sitio 1</h2>
        <table>
            <tr>
                <td><b>Fecha/Hora actual:</b></td>
                <td><%= new Date() %></td>
            </tr>
            <tr>
                <td><b>Método HTTP:</b></td>
                <td><%= request.getMethod() %></td>
            </tr>
            <tr>
                <td><b>URI solicitada:</b></td>
                <td><%= request.getRequestURI() %></td>
            </tr>
            <tr>
                <td><b>IP Cliente:</b></td>
                <td><%= request.getRemoteAddr() %></td>
            </tr>
            <tr>
                <td><b>Session ID:</b></td>
                <td><%= session.getId() %></td>
            </tr>
        </table>
        <p><a href="hello">← Volver al Servlet</a></p>
    </div>
</body>
</html>
