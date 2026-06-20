# ─── Etapa 1: Compilación ────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia solo el pom y descarga dependencias (aprovecha caché de Docker)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copia el código fuente y compila el JAR sin ejecutar los tests
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ─── Etapa 2: Imagen final ────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Confirmado: Calza de forma exacta con tu artifactId y version del pom.xml
COPY --from=builder /app/target/despacho-0.0.1-SNAPSHOT.jar app.jar

# Puerto estándar expuesto para producción
EXPOSE 8080

# Comando de arranque que fuerza la lectura del perfil 'prod' de tu application.yml
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]