# Use a standard, official base image for Java 17
FROM openjdk:17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to leverage Docker's layer caching
COPY pom.xml .

# Download all the project dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the rest of your application's source code
COPY src ./src

# Build the executable "fat JAR"
RUN ./mvnw package

# Expose the port your app will run on
EXPOSE 4567

# The command to run your application
CMD ["java", "-jar", "target/smart-recipe-app-1.0-SNAPSHOT.jar"]

