<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.text.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Sitio 2</title>
    <style>
        body { font-family: Arial; margin: 40px; background: #f1f8e9; }
        .dashboard { background: white; padding: 30px; border-radius: 10px; }
        h2 { color: #33691e; }
        .card { background: #c5e1a5; padding: 15px; margin: 10px 0; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="dashboard">
        <h2>Dashboard JSP - Sitio 2</h2>
        <div class="card">
            <h3>Fecha y Hora</h3>
            <p><%= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) %></p>
        </div>
        <div class="card">
            <h3>Sesión</h3>
            <p><b>ID:</b> <%= session.getId() %></p>
        </div>
        <div class="card">
            <h3>Petición</h3>
            <p><b>IP Cliente:</b> <%= request.getRemoteAddr() %></p>
        </div>
        <p><a href="hello">← Volver</a></p>
    </div>
</body>
</html>
