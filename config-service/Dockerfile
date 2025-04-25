# Dockerfile pour config-service
FROM openjdk:17-jdk-slim
VOLUME /tmp

# Nom du JAR compilé
ARG JAR_FILE=target/config-service*.jar
COPY ${JAR_FILE} app.jar

# Exposer le port (9999 selon application.properties)
EXPOSE 9999

# Commande de lancement
ENTRYPOINT ["java", "-jar", "/app.jar"]
