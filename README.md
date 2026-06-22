# SuperMercado - Arquitectura de Microservicios

Proyecto semestral de la asignatura **DSY1103 - Desarrollo FullStack 1**. Sistema distribuido de 12 microservicios para la gestión de un supermercado, implementado con Spring Boot 3.3.5, Spring Cloud 2023.0.3, Java 21 y Docker.

---

## Integrantes del equipo

| Nombre | Rol | Microservicios |
|--------|-----|----------------|
| *Completar* | *Completar* | *Completar* |
| *Completar* | *Completar* | *Completar* |
| *Completar* | *Completar* | *Completar* |

---

## Microservicios

| # | Microservicio | Puerto | Swagger UI | Descripción |
|---|---------------|--------|------------|-------------|
| 1 | **eureka** | 8761 | — | Service Discovery (Eureka Server) |
| 2 | **gateway** | 8080 | [Swagger](http://localhost:8080/swagger-ui.html) | API Gateway (Spring Cloud Gateway) |
| 3 | **clientes** | 8086 | [Swagger](http://localhost:8086/swagger-ui.html) | Gestión de clientes (CRUD, estados) |
| 4 | **productos** | 8081 | [Swagger](http://localhost:8081/swagger-ui.html) | Catálogo de productos |
| 5 | **pedidos** | 8082 | [Swagger](http://localhost:8082/swagger-ui.html) | Órdenes de compra |
| 6 | **pagos** | 8085 | [Swagger](http://localhost:8085/swagger-ui.html) | Procesamiento de pagos |
| 7 | **inventarios** | 8083 | [Swagger](http://localhost:8083/swagger-ui.html) | Control de stock |
| 8 | **despachos** | 8089 | [Swagger](http://localhost:8089/swagger-ui.html) | Seguimiento de despachos |
| 9 | **catalogo** | 8084 | [Swagger](http://localhost:8084/swagger-ui.html) | Gestión de catálogo y campañas |
| 10 | **proveedores** | 8087 | [Swagger](http://localhost:8087/swagger-ui.html) | Gestión de proveedores |
| 11 | **categorias** | 8090 | [Swagger](http://localhost:8090/swagger-ui.html) | Categorías de productos |
| 12 | **reseñas** | 8088 | [Swagger](http://localhost:8088/swagger-ui.html) | Reseñas y valoraciones de productos |

---

## Gateway Routes

| Ruta | Destino |
|------|---------|
| `/api/v1/productos/**` | `lb://productos` |
| `/api/v1/pedidos/**` | `lb://pedidos` |
| `/api/v1/clientes/**` | `lb://clientes` |
| `/api/inventarios/**` | `lb://inventarios` |
| `/api/despachos/**` | `lb://despachos` |
| `/api/v1/proveedores/**` | `lb://proveedores` |
| `/api/v1/categorias/**` | `lb://categorias` |
| `/api/v1/catalogo/**` | `lb://catalogo` |
| `/api/v1/pagos/**` | `lb://pagos` |
| `/api/v1/resenas/**` | `lb://reseñas` |

---

## Arquitectura

Cada microservicio sigue el patrón **CSR (Controller → Service → Repository/Model)**:

```
controller/    → Manejo HTTP, validación de entrada, delegación al servicio
service/       → Lógica de negocio y reglas del dominio
repository/    → Acceso a datos mediante JPA
model/         → Entidades JPA
dto/           → Objetos de transferencia (Request / Response)
exception/     → Excepciones de dominio + GlobalExceptionHandler
config/        → Swagger, WebClient, Feign
client/        → Feign Clients (comunicación entre microservicios)
```

### Comunicación entre servicios

- **Gateway** → Enrutamiento centralizado con Spring Cloud Gateway + circuit breakers (Resilience4j) + retry
- **Service Discovery** → Eureka para registro y descubrimiento de servicios
- **Feign Clients** → Comunicación directa entre microservicios (clientes ↔ pedidos, reseñas ↔ productos/clientes/pedidos/pagos, etc.)

---

## Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.3.5 |
| Spring Cloud | 2023.0.3 |
| Spring Cloud Gateway | 4.x |
| Spring Data JPA | 3.x |
| Eureka | Netflix |
| MySQL | 8.0 |
| Flyway | 10.x |
| OpenAPI / Swagger | springdoc 2.5.0 / 2.6.0 |
| Lombok | 1.18.46 |
| JUnit 5 | + Mockito |
| JaCoCo | 0.8.11 |
| Docker | Compose |

---

## Cobertura de pruebas (JaCoCo)

Cada microservicio con pruebas unitarias exige un **mínimo del 80% de cobertura de línea** validado automáticamente por JaCoCo durante `mvn verify`.

| Microservicio | Tests | Cobertura |
|---------------|-------|-----------|
| clientes | 24 | ≥80% |
| pagos | 34 | ≥80% |
| productos | 18 | ≥80% |
| pedidos | 15 | ≥80% |
| proveedores | 14 | ≥80% |
| categorias | 12 | ≥80% |
| catalogo | 11 | ≥80% |
| inventarios | 13 | ≥80% |
| despachos | 11 | ≥80% |
| reseñas | 24 | ≥80% |

Para generar el reporte de cobertura:
```bash
cd <microservicio>
mvn clean verify
# Reporte HTML: target/site/jacoco/index.html
```

---

## Ejecución local

### Requisitos previos
- JDK 21
- Maven 3.8+ (o usar `mvnw.cmd` de cada servicio)
- Docker Desktop (opcional, para base de datos)

### Con Docker Compose (recomendado)

```bash
# Clonar el repositorio
git clone https://github.com/18-de-mayo/SuperMercado.git
cd SuperMercado

# Construir y levantar todo el ecosistema
docker-compose up --build
```

Esto levanta:
- 2 contenedores MySQL (uno para categorías, otro para los demás servicios)
- 12 microservicios con Eureka + Gateway
- Todos los servicios se registran automáticamente en Eureka

### Sin Docker (desarrollo)

1. Iniciar MySQL local con las bases de datos necesarias
2. Compilar y ejecutar Eureka primero:
   ```bash
   cd eureka/eureka
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
   ```
3. Compilar y ejecutar Gateway:
   ```bash
   cd gateway
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
   ```
4. Compilar y ejecutar los demás servicios en cualquier orden:
   ```bash
   cd <microservicio>
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
   ```

---

## Variables de entorno (perfil prod)

| Variable | Descripción |
|----------|-------------|
| `DB_URL` | URL JDBC de MySQL |
| `DB_USER` | Usuario de base de datos |
| `DB_PASS` | Contraseña de base de datos |
| `EUREKA_URL` | URL del servidor Eureka |
| `SERVER_PORT` | Puerto del servicio |

---

## Documentación de APIs

Cada microservicio expone Swagger UI en:

- `http://localhost:<puerto>/swagger-ui.html`
- `http://localhost:<puerto>/v3/api-docs`

También están centralizadas a través del Gateway en `http://localhost:8080/swagger-ui.html`.

---

## Gestión del proyecto (Trello)

Tablero Trello del equipo: [*Completar con enlace*]

---

## Licencia

Proyecto académico - DSY1103 Desarrollo FullStack 1
