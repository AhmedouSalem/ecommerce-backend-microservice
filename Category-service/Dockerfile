FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/Category-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app.jar"]
