FROM openjdk:21-jdk-slim
LABEL authors="kyawyelwin"
WORKDIR /app
COPY target/SpringBootSonarQube-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]