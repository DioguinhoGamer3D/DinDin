# ---------- Estágio 1: compila o projeto ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependências primeiro; esta camada fica em cache enquanto o pom.xml não mudar
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -Dmaven.test.skip=true package

# ---------- Estágio 2: imagem final, só com o necessário para rodar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 1001 dindin
COPY --from=build /app/target/*.jar app.jar
USER dindin

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+UseSerialGC", "-jar", "app.jar"]