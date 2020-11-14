# Dockerfile based on https://medium.com/@nieldw/caching-maven-dependencies-in-a-docker-build-dca6ca7ad612

# Step : Test and package
FROM maven:3.5-jdk-8 as target
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests=true

# Step : Package image
FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY --from=target /app/target/app.jar /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]