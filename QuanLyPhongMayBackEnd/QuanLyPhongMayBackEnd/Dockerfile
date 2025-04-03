# --- Stage 1: Build ---
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests

# --- Stage 2: Runtime ---
# SỬA DÒNG NÀY: Dùng tag 'openjdk:17-slim' thay thế
FROM openjdk:17-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# Hoặc: ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]