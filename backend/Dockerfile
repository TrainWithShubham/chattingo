# Stage 1: Maven build environment
FROM maven:3.9-openjdk-17 AS build-env
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline

# Stage 2: Build Spring Boot application
FROM maven:3.9-openjdk-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 3: JRE runtime
FROM openjdk:17-jre-slim AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
