FROM maven:3.9-eclipse-temurin:17 AS buil

WORKDIR /app

COPY . .
RUN mvn clean package -DskipTest

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jap
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
