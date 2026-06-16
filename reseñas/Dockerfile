# ─── Etapa 1: Build ────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar solo el pom primero para aprovechar caché de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el resto del código y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─── Etapa 2: Runtime ───────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiar el JAR generado en la etapa anterior
COPY --from=builder /app/target/resena-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto (debe coincidir con server.port en application.yml)
EXPOSE 8087

# Variables de entorno configurables en tiempo de ejecución
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8087

# Comando de arranque
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "app.jar"]
