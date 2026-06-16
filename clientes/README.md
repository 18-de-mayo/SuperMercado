# 🧑‍💼 Microservicio Cliente

Microservicio REST desarrollado con **Spring Boot 3** para la gestión de clientes dentro de la arquitectura distribuida del proyecto semestral de DSY1103.

---

## 👥 Integrantes del equipo

> Completar con los nombres del equipo

---

## 📋 Descripción

Gestiona el ciclo de vida completo de los clientes: registro, actualización, cambio de estado y consultas. Expone un endpoint interno (`/activo`) para que otros microservicios (como **pedidos**) verifiquen si un cliente puede operar.

---

## 🧩 Microservicios del proyecto

| Microservicio     | Puerto | Descripción                        |
|-------------------|--------|------------------------------------|
| **cliente**       | 8081   | Gestión de clientes ← *este*       |
| pedidos           | 8082   | Gestión de órdenes de compra       |
| productos         | 8083   | Catálogo de productos              |
| catalogo          | 8084   | Gestión del catálogo               |
| inventario        | 8085   | Control de stock                   |
| despacho          | 8086   | Seguimiento de despachos           |
| pagos             | 8087   | Procesamiento de pagos             |
| proveedor         | 8088   | Gestión de proveedores             |
| resena            | 8089   | Reseñas de productos               |
| categoria         | 8090   | Categorías de productos            |

---

## 🏗️ Arquitectura

El microservicio sigue el patrón **CSR (Controller → Service → Repository)**:

```
controller/   → Manejo HTTP, validación de entrada, delegación al servicio
service/      → Lógica de negocio y reglas del dominio
repository/   → Acceso a datos mediante JPA
model/        → Entidad JPA Cliente
dto/          → Objetos de transferencia (Request / Response)
exception/    → Excepciones de dominio + GlobalExceptionHandler
config/       → Swagger, WebClient
```

---

## 📐 Reglas de negocio

1. El **email** de cada cliente debe ser único en el sistema.
2. El **RUT** de cada cliente debe ser único en el sistema.
3. Un cliente puede tener estado: `ACTIVO`, `INACTIVO` o `SUSPENDIDO`.
4. Un cliente con estado **SUSPENDIDO no puede pasar directamente a ACTIVO**; debe transitar primero por INACTIVO.
5. Solo clientes `ACTIVO` pueden realizar pedidos (verificado mediante endpoint `/api/clientes/{id}/activo`).

---

## 🔗 Endpoints principales

| Método   | Ruta                              | Descripción                          |
|----------|-----------------------------------|--------------------------------------|
| `POST`   | `/api/clientes`                   | Registrar nuevo cliente              |
| `GET`    | `/api/clientes`                   | Listar todos los clientes            |
| `GET`    | `/api/clientes/{id}`              | Obtener cliente por ID               |
| `GET`    | `/api/clientes/email/{email}`     | Obtener por email                    |
| `GET`    | `/api/clientes/rut/{rut}`         | Obtener por RUT                      |
| `GET`    | `/api/clientes/estado/{estado}`   | Filtrar por estado                   |
| `GET`    | `/api/clientes/buscar?texto=`     | Buscar por nombre o apellido         |
| `GET`    | `/api/clientes/{id}/activo`       | Verificar si el cliente está activo  |
| `PUT`    | `/api/clientes/{id}`              | Actualizar datos del cliente         |
| `PATCH`  | `/api/clientes/{id}/estado`       | Cambiar estado                       |
| `DELETE` | `/api/clientes/{id}`              | Eliminar cliente                     |

### Gateway routes (cuando aplique)

```
/clientes/** → http://localhost:8081
```

---

## 📖 Documentación Swagger

- **Local:** http://localhost:8081/swagger-ui.html
- **API Docs JSON:** http://localhost:8081/v3/api-docs

---

## 🚀 Ejecución local

### Requisitos previos

- Java 17+
- Maven 3.8+
- MySQL 8.0 corriendo en `localhost:3306`

### Pasos

```bash
# 1. Crear la base de datos (si no existe)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS cliente_db;"

# 2. Compilar el proyecto
mvn clean install

# 3. Ejecutar con perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

El servicio queda disponible en: `http://localhost:8081`

---

## 🐳 Ejecución con Docker

```bash
# Levantar servicio + base de datos
docker-compose up --build

# Solo la base de datos
docker-compose up cliente-db

# Detener y limpiar
docker-compose down -v
```

---

## ☁️ Despliegue remoto (Railway / Render)

### Variables de entorno requeridas

| Variable            | Descripción                         |
|---------------------|-------------------------------------|
| `DATABASE_URL`      | URL JDBC de la base de datos remota |
| `DATABASE_USERNAME` | Usuario de la base de datos         |
| `DATABASE_PASSWORD` | Contraseña de la base de datos      |
| `PORT`              | Puerto del servidor (default 8081)  |

```bash
# Perfil prod (usa variables de entorno)
java -jar cliente-1.0.0.jar --spring.profiles.active=prod
```

---

## 🧪 Pruebas unitarias

```bash
# Ejecutar tests
mvn test

# Ejecutar tests + reporte de cobertura JaCoCo
mvn verify

# Ver reporte HTML
open target/site/jacoco/index.html
```

Cobertura mínima configurada: **80%** (validada automáticamente por JaCoCo).

---

## 🔧 Tecnologías

- Spring Boot 3.2.5
- Spring Data JPA + Hibernate
- MySQL 8 / H2 (tests)
- Bean Validation (JSR 380)
- WebClient (comunicación con pedidos/despacho)
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 + Mockito
- JaCoCo (cobertura)
- Docker + Docker Compose
- Lombok
- SLF4J (logs estructurados)
