# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the project's build file and source code to the container
COPY pom.xml /app
COPY src /app/src

# Package the application using Maven
RUN apt-get update && \
    apt-get install -y maven && \
    mvn -f /app/pom.xml clean package

# Copy the packaged jar file to the container
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]