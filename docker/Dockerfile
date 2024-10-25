FROM openjdk:21-slim-buster AS build

LABEL authors="viesant"

# Traz todos os arquivos onde o Dockerfile está
COPY .. .

# Transforma o mvnw em executável
RUN chmod 700 mvnw

RUN ./mvnw clean package -Dspring.profiles.active=test

FROM openjdk:21-slim-buster

WORKDIR /app

COPY --from=build target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]