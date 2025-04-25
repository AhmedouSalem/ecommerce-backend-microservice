FROM openjdk:17-jdk-slim
VOLUME /tmp

ARG JAR_FILE=target/coupon-service*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]
