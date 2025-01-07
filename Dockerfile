FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-21-jdk -y

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew build -x test

EXPOSE 8080

ENTRYPOINT ["java", "-jar",  "/build/libs/back-0.0.1-SNAPSHOT.jar"]