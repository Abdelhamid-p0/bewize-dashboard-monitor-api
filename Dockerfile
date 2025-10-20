# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy only pom first to leverage dependency caching
COPY pom.xml ./
RUN mvn -B -DskipTests=true dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests=true package && cp target/*.jar app.jar

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Runtime configuration
ENV JAVA_OPTS=""
ENV SERVER_PORT=8080

# Copy built jar
COPY --from=builder /app/app.jar /app/app.jar

# Expose the runtime port (can be overridden via SERVER_PORT)
EXPOSE 8080

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=$SERVER_PORT -jar /app/app.jar"]