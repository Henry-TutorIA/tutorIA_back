FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-21-jdk -y

COPY . .

RUN ./gradlew build

EXPOSE 8080

ENTRYPOINT ["java", "-jar",  "/build/libs/sw-0.0.1-SNAPSHOT.jar"]