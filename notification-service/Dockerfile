FROM openjdk:21-bullseye AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests


FROM openjdk:21-bullseye

WORKDIR /app

COPY --from=build /app/target/*.jar /app/notification-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]