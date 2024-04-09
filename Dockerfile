FROM ubuntu:latest
LABEL authors="yurii kotsiuba"

FROM gradle:7.5.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim

EXPOSE 9090

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/bookstore-0.0.1-SNAPSHOT.jar /app/bookstore-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "/app/bookstore-0.0.1-SNAPSHOT.jar"]