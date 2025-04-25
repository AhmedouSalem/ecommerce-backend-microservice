FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/product-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app.jar"]
