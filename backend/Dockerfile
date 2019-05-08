FROM openjdk:8-jdk-alpine
EXPOSE 8080
WORKDIR /app
ADD /target/final.jar /app
ENTRYPOINT ["/usr/bin/java", "-jar", "final.jar"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]


#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#ARG DEPENDENCY=target/dependency
#COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY ${DEPENDENCY}/META-INF /app/META-INF
#COPY ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","pl.reryk.linkzone.Application"]