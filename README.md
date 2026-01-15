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

## 📡 Configuración de Acceso Estándar (Puertos 80 y 443)

> **Nota:** Esta configuración es **opcional** y **no está implementada** en la versión principal del proyecto. Se documenta como alternativa para entornos que requieran URLs sin especificación de puertos.

---

### Contexto

Para que el servidor sea accesible mediante URLs limpias (sin especificar puertos como `:8080`), es necesaria una reconfiguración integral en tres niveles:

1. **Orquestador** (Docker Compose)
2. **Servidor de aplicaciones** (Tomcat)
3. **Sistema operativo** (Ubuntu)

---

### 1️⃣ Reajuste del Mapeo de Puertos (Port Forwarding)

En el archivo `docker-compose.yml`, se modifican los puertos mapeados para usar los estándares de HTTP/HTTPS:

```yaml
ports:
  - "80:8080" # HTTP estándar → Tomcat HTTP
  - "443:8443" # HTTPS estándar → Tomcat HTTPS
  - "8009:8009" # AJP (sin cambios)
```

**¿Qué logra esto?**

- El usuario accede con `http://sitio1.local` (sin puerto)
- Docker redirige internamente al puerto 8080 de Tomcat
- Tomcat procesa la petición sin saber que externamente es el puerto 80

---

### 2️⃣ Sincronización del server.xml

#### Redirección HTTPS

```xml
<Connector port="8080" protocol="HTTP/1.1"
    redirectPort="8443" />
```

**Funcionamiento:**

- Si una petición HTTP requiere seguridad (ej: acceso al Manager), Tomcat redirige internamente a `8443`
- Docker traduce esa respuesta al puerto `443` externo
- El navegador recibe una redirección a `https://sitio1.local` (sin puerto visible)

#### Optimización de Conectores (Opcional)

Con Virtual Hosts correctamente configurados, **no es necesario** mantener múltiples conectores HTTP (8080 y 8081):

```xml
<!-- UN SOLO conector HTTP gestiona todos los hosts virtuales -->
<Connector port="8080" protocol="HTTP/1.1" ... />

<!-- Los Virtual Hosts se diferencian por nombre de dominio -->
<Host name="sitio1.local" ... />
<Host name="sitio2.local" ... />
```

**Ventaja:** Reducción de consumo de recursos (menos threads, menos sockets).

---

### 3️⃣ Resolución de Conflictos en el Sistema Operativo

#### Puertos Privilegiados (<1024)

En Linux, solo `root` puede usar puertos por debajo del 1024. Para que Docker los use:

```bash
# Otorgar permisos al daemon de Docker (ya configurado por defecto)
sudo setcap 'cap_net_bind_service=+ep' /usr/bin/dockerd
```

#### Liberar Puerto 80 (si está ocupado)

```bash
# Identificar proceso usando el puerto 80
sudo lsof -i :80

# Detener proceso específico
sudo fuser -k 80/tcp

# Verificar que está libre
sudo netstat -tuln | grep :80
```

**Causas comunes de conflicto:**

- Apache HTTP Server (`apache2`)
- Nginx
- Contenedores Docker huérfanos

---

### 4️⃣ Tabla de Acceso con Puertos Estándar

| Servicio                 | URL                                   | Puerto | Protocolo |
| ------------------------ | ------------------------------------- | ------ | --------- |
| **Sitio 1**              | `http://sitio1.local/hello`           | 80     | HTTP      |
| **Sitio 1 (HTTPS)**      | `https://sitio1.local/hello`          | 443    | HTTPS     |
| **Sitio 2**              | `http://sitio2.local/hello`           | 80     | HTTP      |
| **Sitio 2 (HTTPS)**      | `https://sitio2.local/hello`          | 443    | HTTPS     |
| **Manager**              | `http://localhost/manager/html`       | 80     | HTTP      |
| **Manager (HTTPS)**      | `https://localhost/manager/html`      | 443    | HTTPS     |
| **Host Manager**         | `http://localhost/host-manager/html`  | 80     | HTTP      |
| **Host Manager (HTTPS)** | `https://localhost/host-manager/html` | 443    | HTTPS     |

---

### ⚙️ Implementación Paso a Paso

#### 1. Modificar `docker-compose.yml`

```yaml
services:
  tomcat:
    build: .
    container_name: tomcat-jorge-ud3
    ports:
      - "80:8080"
      - "443:8443"
      - "8009:8009"
    # ... resto de la configuración
```

#### 2. Reconstruir y reiniciar

```bash
docker compose down
docker compose up --build -d
```

#### 3. Verificar acceso

```bash
curl http://sitio1.local/hello
curl -k https://sitio1.local/hello  # -k ignora certificado autofirmado
```

---

### ⚠️ Consideraciones

**Ventajas:**

- ✅ URLs profesionales sin puertos visibles
- ✅ Compatibilidad con expectativas de usuarios finales
- ✅ Facilita integración con DNS públicos

**Desventajas:**

- ⚠️ Requiere permisos elevados en el host
- ⚠️ Posibles conflictos con servicios existentes (Apache, Nginx)
- ⚠️ Complejidad adicional en troubleshooting

**Recomendación:**

- **Desarrollo:** Usar puertos no privilegiados (8080, 8443) como en la versión principal
- **Producción:** Implementar proxy reverso (Nginx/Apache) delante de Tomcat en lugar de mapeo directo de puertos

## **Estado:** Fin Configuración opcional no incluida en entrega principal

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
git clone https://github.com/JcDevProject24/docker-tomcat11-multi-vhost.git
cd docker-tomcat11-multi-vhost.git
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
├── license.md
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
