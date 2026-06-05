FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY common ./common
COPY gateway-service ./gateway-service
COPY auth-service ./auth-service
COPY user-service ./user-service
COPY nutrition-service ./nutrition-service
COPY analytics-service ./analytics-service
COPY recommendation-service ./recommendation-service
COPY leaderboard-service ./leaderboard-service
COPY achievement-service ./achievement-service
COPY notification-service ./notification-service
ARG MODULE
RUN mvn -B -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG MODULE
COPY --from=build /workspace/${MODULE}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

