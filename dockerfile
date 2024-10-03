FROM eclipse-temurin:17-jre

WORKDIR /app

VOLUME ["/app/data", "/app/config"]

COPY target/*-fat.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
