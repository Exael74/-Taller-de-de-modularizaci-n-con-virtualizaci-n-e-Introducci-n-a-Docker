# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /usrapp/bin

# Set port environment variable
ENV PORT 6000

# Copy the JAR from the build stage
COPY --from=build /app/target/custom-web-framework-1.0-SNAPSHOT.jar /usrapp/bin/app.jar
COPY --from=build /app/target/dependency /usrapp/bin/dependency
COPY --from=build /app/src/main/resources/public /usrapp/bin/src/main/resources/public

# Run the application
# We use the main class directly or the JAR
CMD ["java", "-cp", "app.jar:dependency/*", "co.edu.escuelaing.app.Main"]
