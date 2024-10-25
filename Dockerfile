FROM openjdk:21-slim-buster AS build

LABEL authors="viesant"

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o wrapper do Maven, o arquivo pom.xml e a pasta src do projeto
COPY . .

# Torna o mvnw executável
RUN chmod +x mvnw

# Executa o build usando Maven
RUN ./mvnw clean package

FROM openjdk:21-slim-buster

WORKDIR /app

COPY --from=build target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]