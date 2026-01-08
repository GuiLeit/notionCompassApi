# Use Java 25 base image
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

# Copy Maven/Gradle wrapper and dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Install bash
RUN apk add --no-cache \
    maven \
    bash

COPY pom.xml .

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE ${SERVER_PORT:-8080}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]