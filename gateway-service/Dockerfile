# Dockerfile - gateway-service
FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/gateway-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8891

ENTRYPOINT ["java", "-jar", "/app.jar"]
