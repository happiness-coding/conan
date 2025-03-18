FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# First copy only the POM to leverage Docker cache
COPY pom.xml .

# Check if .mvn directory exists before copying
COPY src src

# Use Maven directly instead of wrapper
RUN apk add --no-cache maven && \
    mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Add wait-for-it script to wait for the database
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]