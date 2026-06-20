# ─── Etapa 1: Compilación ────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ─── Etapa 2: Imagen final de ejecución ──────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# El Gateway expone el puerto público de entrada al ecosistema
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]