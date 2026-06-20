# ─── Etapa 1: Compilación ────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia solo el pom y descarga dependencias (aprovecha la caché de capas de Docker)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copia el código fuente y compila el JAR omitiendo los tests para acelerar el despliegue
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ─── Etapa 2: Imagen final de ejecución ──────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# AJUSTE: Copiamos usando un comodín (*.jar) para evitar fallos si cambia la versión en el pom
COPY --from=builder /app/target/*.jar app.jar

# AJUSTE: Exponemos el puerto real de categorías configurado en tu application.yml
EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]