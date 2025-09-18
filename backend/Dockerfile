# Stage 1: build with Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the project and skip tests
RUN mvn clean package -DskipTests

# Stage 2: run with JDK
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/mealquest-backend-1.0.0.jar app.jar

# Expose the port your Spring Boot app uses
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java","-jar","app.jar"]
