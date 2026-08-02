# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper + dependencias
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Codigo fuente
COPY src src

# Compilar el jar
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
