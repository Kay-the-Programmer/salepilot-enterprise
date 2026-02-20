# Multi-stage build for optimized image size
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy gradle files for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the application
RUN gradle clean build -x test --no-daemon

# Runtime stage - use slim JRE image
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN groupadd -r salepilot && useradd -r -g salepilot salepilot

# Copy built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R salepilot:salepilot /app

# Switch to non-root user
USER salepilot

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
