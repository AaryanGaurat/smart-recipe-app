# Use a standard, official base image for Java 17
FROM openjdk:17-slim

# Set the working directory inside the container
WORKDIR /app

# Copy all the project files into the app directory
COPY . .

# Force executable permission on the Maven Wrapper ---
# This command runs on the Render server and guarantees the script is runnable.
RUN chmod +x mvnw

# Download all the project dependencies
RUN ./mvnw dependency:go-offline -B

# Build the executable "fat JAR"
RUN ./mvnw package

# Expose the port your app will run on
EXPOSE 4567

# The command to run your application
CMD ["java", "-jar", "target/smart-recipe-app-1.0-SNAPSHOT.jar"]

