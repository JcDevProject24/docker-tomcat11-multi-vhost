# 1. Imagen base
FROM ubuntu:24.04

# 2. Variables de entorno
ENV DEBIAN_FRONTEND=noninteractive
ENV JAVA_HOME=/opt/jdk25
ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH

# 3. Herramientas e instalación de Java 25
RUN apt-get update && apt-get install -y wget tar curl && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /opt && cd /opt && \
    wget https://github.com/adoptium/temurin25-binaries/releases/download/jdk-25.0.1%2B8/OpenJDK25U-jdk_x64_linux_hotspot_25.0.1_8.tar.gz && \
    tar -xzf OpenJDK25U-jdk_x64_linux_hotspot_25.0.1_8.tar.gz && \
    mv jdk-25.0.1+8 $JAVA_HOME && rm OpenJDK25U-jdk_x64_linux_hotspot_25.0.1_8.tar.gz

# 4. Instalación de Tomcat 11
RUN cd /opt && \
    wget https://archive.apache.org/dist/tomcat/tomcat-11/v11.0.2/bin/apache-tomcat-11.0.2.tar.gz && \
    tar -xzf apache-tomcat-11.0.2.tar.gz && \
    mv apache-tomcat-11.0.2 tomcat && \
    rm apache-tomcat-11.0.2.tar.gz

# Crear las carpetas que espera el server.xml
RUN mkdir -p $CATALINA_HOME/webapps/sitio1 $CATALINA_HOME/webapps/sitio2
# 5. Configuración de aplicaciones y permisos
# COPY Sitio1/target/sitio1.war $CATALINA_HOME/webapps/sitio1.war
# COPY Sitio2/target/sitio2.war $CATALINA_HOME/webapps/sitio2.war
# Copiar los archivos DENTRO de sus carpetas correspondientes
COPY Sitio1/target/sitio1.war $CATALINA_HOME/webapps/sitio1/ROOT.war
COPY Sitio2/target/sitio2.war $CATALINA_HOME/webapps/sitio2/ROOT.war
COPY conf/keystore.jks $CATALINA_HOME/conf/keystore.jks

# Creamos las carpetas de destino para que el COPY no falle
RUN mkdir -p $CATALINA_HOME/webapps/manager/META-INF && \
    mkdir -p $CATALINA_HOME/webapps/host-manager/META-INF

# Copiamos tu context.xml (el que tiene la persistencia y límites)
COPY context.xml $CATALINA_HOME/webapps/manager/META-INF/context.xml
COPY context.xml $CATALINA_HOME/webapps/host-manager/META-INF/context.xml

RUN chmod -R 755 $CATALINA_HOME && chmod +x $CATALINA_HOME/bin/*.sh

EXPOSE 8080 8081 8009 8443
CMD ["catalina.sh", "run"]