# Etapa de compilación
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /ms-users
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /ms-users
COPY --from=build /ms-users/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]