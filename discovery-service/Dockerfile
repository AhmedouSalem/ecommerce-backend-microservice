# Dockerfile - discovery-service
FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/discovery-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app.jar"]
