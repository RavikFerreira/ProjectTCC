FROM openjdk:21
COPY target/orchestrator-service-0.1.jar app.jar
EXPOSE 8585
ENTRYPOINT ["java", "-jar", "app.jar"]