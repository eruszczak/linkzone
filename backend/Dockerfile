# 330 MB
FROM maven:3.5-jdk-8-alpine as target

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app
COPY pom.xml .
# RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests=true

# Step : Package image
FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY --from=target /app/target/app.jar /app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]




# 650 MB

# FROM maven:3.5-jdk-8-alpine

# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring:spring

# WORKDIR /app
# COPY pom.xml .
# RUN mvn dependency:go-offline

# COPY src ./src
# RUN mvn package -DskipTests=true

# EXPOSE 8080
# ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/target/app.jar"]