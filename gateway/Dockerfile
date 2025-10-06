# Stage 1: Build
FROM gradle:9.0-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle bootJar

# Stage 2: Run
FROM openjdk:25-slim
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
