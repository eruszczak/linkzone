#FROM amazoncorretto:18-alpine-jdk
FROM openjdk:18-jre-alpine
WORKDIR /app
COPY target/linkzone-*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]