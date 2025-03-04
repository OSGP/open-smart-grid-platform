FROM eclipse-temurin:17-jdk-alpine
COPY target/*.war /app/app.war
RUN chmod 777 /app/
ENTRYPOINT ["java", "-jar", "/app/app.war"]