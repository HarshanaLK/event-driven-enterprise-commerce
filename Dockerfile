FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
COPY common-events/pom.xml common-events/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY inventory-service/pom.xml inventory-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml

COPY common-events/src common-events/src
COPY order-service/src order-service/src
COPY inventory-service/src inventory-service/src
COPY payment-service/src payment-service/src
COPY notification-service/src notification-service/src

ARG MODULE
RUN mvn -q -pl "${MODULE}" -am clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app
ARG MODULE
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
