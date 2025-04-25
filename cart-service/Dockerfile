FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/cart-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]
