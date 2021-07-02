# We will base our Docker image on the official
# Docker image for OpenJDK and use the Java SE
# v12.0.2
FROM openjdk:12.0.2

# We expose port 8080 to other Docker containers
EXPOSE 8080

# We add our fat-jar file to the Docker image's
# root from the Gradble build library, "build/libs".
# We name it "app.jar". It will appear as "/app.jar"
# in the docker container.
ADD ./build/libs/*.jar app.jar

# Specifying the command to be used by Docker
# when a container is started up using this
# Docker image. `java -jar /app.jar`.
ENTRYPOINT ["java", "-jar", "/app.jar"]