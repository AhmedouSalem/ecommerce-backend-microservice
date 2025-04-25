FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/review-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]
