# ─── Etapa 1: Compilación ────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia solo el pom y descarga dependencias (aprovecha caché de Docker)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copia el código fuente y compila el JAR
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ─── Etapa 2: Imagen final ────────────────────────────────────────────────────
FROM eclipse-temurin:26-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/proveedor-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]