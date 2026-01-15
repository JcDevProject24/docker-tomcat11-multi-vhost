# 🚀 Práctica UD3 - Apache Tomcat 11 en Docker

> **Despliegue profesional de aplicaciones Jakarta EE con hosts virtuales, SSL/TLS y gestión avanzada de recursos**

Este proyecto implementa una infraestructura completa de **Apache Tomcat 11** sobre **Docker**, utilizando **OpenJDK 25** y configuraciones empresariales de seguridad, rendimiento y aislamiento de aplicaciones.

---

## 📋 Características Técnicas

- **Runtime de última generación**: OpenJDK 25 (Temurin Hotspot) y Apache Tomcat 11.0.2
- **Virtual Hosting**: Aislamiento completo entre `sitio1.local` y `sitio2.local` mediante `appBase` dedicados
- **Seguridad reforzada**: SSL/TLS (puerto 8443), autenticación basada en roles (RBAC), y validación opcional de certificados cliente
- **Optimización**: Thread pools configurados (250 threads HTTP), buffers de 16KB, y persistencia de sesiones
- **Auditoría**: Logs independientes por host virtual con métricas de rendimiento (`%D` para tiempo de respuesta)

---

## 🛠️ Arquitectura de Archivos

| Archivo              | Responsabilidad                                                                                     |
| -------------------- | --------------------------------------------------------------------------------------------------- |
| `Dockerfile`         | Construcción de la imagen: Ubuntu 24.04 + Java 25 + Tomcat 11 + empaquetado de WARs como `ROOT.war` |
| `docker-compose.yml` | Orquestación: mapeo de puertos (8080, 8081, 8443, 8009), volúmenes y gestión del ciclo de vida      |
| `server.xml`         | Configuración del servidor: conectores HTTP/HTTPS/AJP, hosts virtuales y parámetros de rendimiento  |
| `tomcat-users.xml`   | Base de datos de usuarios con roles jerárquicos (`admin`, `manager`, `viewer`)                      |
| `context.xml`        | Límites de recursos (caché 100MB) y habilitación del acceso remoto al Manager                       |
| `conf/keystore.jks`  | Certificado SSL autofirmado (RSA 2048 bits, validez 365 días)                                       |

---

## 🌐 Acceso a las Aplicaciones

### Configuración Previa

Agregar al archivo `/etc/hosts`:

```
127.0.0.1   sitio1.local
127.0.0.1   sitio2.local
```

### URLs de Acceso

| Servicio            | URL                                       | Puerto | Protocolo |
| ------------------- | ----------------------------------------- | ------ | --------- |
| **Sitio 1**         | `http://sitio1.local:8080/hello`          | 8080   | HTTP      |
| **Sitio 2**         | `http://sitio2.local:8081/hello`          | 8081   | HTTP      |
| **Sitio 1 (HTTPS)** | `https://sitio1.local:8443/hello`         | 8443   | HTTPS     |
| **Manager**         | `http://localhost:8080/manager/html`      | 8080   | HTTP      |
| **Host Manager**    | `http://localhost:8080/host-manager/html` | 8080   | HTTP      |

---

## 🔐 Credenciales de Administración

| Usuario   | Contraseña   | Roles Asignados                                                                               |
| --------- | ------------ | --------------------------------------------------------------------------------------------- |
| `admin`   | `admin123`   | `manager-gui`, `manager-script`, `manager-jmx`, `manager-status`, `admin-gui`, `admin-script` |
| `manager` | `manager123` | `manager-gui`, `manager-status`                                                               |
| `viewer`  | `viewer123`  | `viewer`, `manager-status`                                                                    |

---

## 🚀 Despliegue Rápido

### 1️⃣ Compilar las aplicaciones

```bash
cd Sitio1 && mvn clean package && cd ..
cd Sitio2 && mvn clean package && cd ..
```

### 2️⃣ Construir y levantar el entorno

```bash
docker-compose up --build -d
```

### 3️⃣ Verificar el estado

```bash
docker logs -f tomcat-jorge-ud3
```

### 4️⃣ Acceder a las aplicaciones

Abrir navegador en las URLs indicadas arriba.

---

## ⚡ Pruebas de Rendimiento

Validación de capacidad de respuesta con **Apache Bench**:

```bash
# Instalar Apache Bench
sudo apt-get install apache2-utils

# Ejecutar prueba de carga (1000 peticiones, 50 concurrentes)
ab -n 1000 -c 50 http://sitio1.local:8080/hello
```

### Verificación de Logs Independientes

Los logs de auditoría se generan en `./logs/`:

- `sitio1_access.2026-01-15.log` → Accesos a Sitio 1
- `sitio2_access.2026-01-15.log` → Accesos a Sitio 2
- `localhost_access_log.txt` → Accesos al Manager

---

## 📊 Componentes de las Aplicaciones

Ambos sitios incluyen:

- ✅ **Servlets**: Lógica de negocio con información del sistema (Sitio 1) y timestamp (Sitio 2)
- ✅ **JSP**: Renderizado dinámico de interfaces (`info.jsp`, `dashboard.jsp`)
- ✅ **Filtros**: Logging de peticiones y autenticación
- ✅ **Listeners**: Eventos de ciclo de vida de aplicación y sesiones
- ✅ **Gestión de sesiones**: Persistencia configurada con cookies HTTP-only

---

## 🗺️ Mapa de Conectores y Puertos

```
┌─────────────────────────────────────────────────────────────┐
│                    Apache Tomcat 11.0.2                      │
│                      (tomcat-jorge-ud3)                      │
└─────────────────────────────────────────────────────────────┘
           │                  │                  │
           ▼                  ▼                  ▼
     ┌──────────┐      ┌──────────┐      ┌──────────┐
     │ HTTP:8080│      │ HTTP:8081│      │HTTPS:8443│
     │   sitio1 │      │   sitio2 │      │  sitio1  │
     └──────────┘      └──────────┘      └──────────┘
           │                  │                  │
           ▼                  ▼                  ▼
    sitio1.local       sitio2.local       sitio1.local
    /webapps/sitio1    /webapps/sitio2    (SSL/TLS)

    ┌──────────┐      ┌──────────────────────┐
    │ AJP:8009 │      │ localhost:8080       │
    │  (proxy) │      │ /manager             │
    └──────────┘      │ /host-manager        │
                      └──────────────────────┘
```

---

## 🎯 Decisiones Técnicas Destacadas

1. **ROOT.war**: Los WARs se renombran a `ROOT.war` para servir las apps en la raíz de cada host (`/` en lugar de `/sitio1`)
2. **clientAuth="want"**: Permite validación de certificados cliente sin bloquear conexiones sin certificado
3. **Volúmenes sincronizados**: `context.xml` se monta en Manager y Host-Manager para aplicar límites de recursos globalmente
4. **Thread Pool**: 250 threads en conectores HTTP para soportar alta concurrencia sin degradación

---

## 🛡️ Consideraciones de Seguridad

- El certificado SSL es **autofirmado** (válido para desarrollo, no para producción)
- El `context.xml` permite acceso remoto al Manager (`.* regex`) - **restringir IPs en producción**
- Las contraseñas están en texto plano en `tomcat-users.xml` - considerar **encriptación PBKDF2**

---

## 🔄 Replicación y Escalabilidad

Para garantizar la portabilidad y permitir la replicación del entorno en diferentes nodos, el proyecto se ha versionado y containerizado:

### 1. Clonar el repositorio (GitHub)

Cualquier desarrollador puede replicar esta infraestructura completa en segundos:

```bash
git clone [https://github.com/tu-usuario/nombre-repo.git](https://github.com/tu-usuario/nombre-repo.git)
cd nombre-repo
docker-compose up -d
```

## 🔄 Subida y configuración de GitHub

### 1. Crear .gitignore (Imprescindible)

Crea un archivo llamado .gitignore en la raíz de tu proyecto necesario para quitar los logs de la carpeta lo cual es muy pesado asi como los war etc

```bash
# Logs del sistema y de Tomcat
logs/
*.log
*.txt

# Binarios de Java y carpetas de Maven
target/
*.war
*.jar
*.class

# Archivos de entorno y SO
.DS_Store
Thumbs.db
.env

# Archivos temporales de Docker
.docker/
```

### 2. Para subir un proyecto a git (opcional)

```bash
# 1. Inicializar el repositorio
git init

# 2. Configurar usuario (solo si no se ha hecho antes)
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"

# 3. Añadir archivos al "stage" (el .gitignore filtrará lo innecesario)
git add .

# 4. Primer commit
git commit -m "Initial commit: Infraestructura Tomcat 11 con Virtual Hosts y SSL"

# 5. Vincular con GitHub (crea un repo vacío en GitHub antes)
git branch -M main
git remote add origin https://github.com/tu-usuario/tu-repositorio.git

# 6. Subir los archivos
git push -u origin main

```

## 📦 Estructura del Proyecto

```
Practica_UD3_Jorge/
├── Servidor Web TomCat.pdf
├── Dockerfile
├── README.md
├── docker-compose.yml
├── server.xml
├── tomcat-users.xml
├── context.xml
├── conf/
│   └── keystore.jks
├── logs/
├── Sitio1/
│   ├── pom.xml
│   ├── src/main/java/com/sitio1/
│   │   ├── HelloServlet.java
│   │   ├── LoggingFilter.java
│   │   └── AppListener.java
│   ├── src/main/webapp/
│   │   ├── info.jsp
│   │   └── WEB-INF/web.xml
│   └── target/sitio1.war
└── Sitio2/
    ├── pom.xml
    ├── src/main/java/com/sitio2/
    │   ├── HelloServlet.java
    │   ├── AuthFilter.java
    │   └── SessionListener.java
    ├── src/main/webapp/
    │   ├── dashboard.jsp
    │   └── WEB-INF/web.xml
    └── target/sitio2.war
```

---

## 🔧 Comandos Útiles

```bash
# Ver logs en tiempo real
docker logs -f tomcat-jorge-ud3

# Reiniciar el contenedor
docker-compose restart

# Detener y eliminar
docker-compose down

# Recompilar y redesplegar
mvn clean package -f Sitio1/pom.xml
docker-compose up --build -d
```

---

**Autor**: Jorge  
**Fecha**: Enero 2026  
**Curso**: 2º DAW - Despliegue de Aplicaciones Web (UD3)  
**IES**: Matemático Puig Adam
