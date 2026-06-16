# Microservicio de Reseñas — Supermercado

## Descripción
Microservicio independiente que gestiona las reseñas de productos del supermercado. Permite a los clientes calificar y comentar productos que han comprado, y al equipo de moderación aprobar, rechazar o responder dichas reseñas.

Forma parte de un sistema de arquitectura distribuida de 10 microservicios para la gestión de un supermercado.

## Integrantes del equipo
- Jorge (Clientes, Pagos, Reseñas)
- [Compañero 1] (Productos, Proveedores, Categorías)
- [Compañero 2] (Catálogo, Inventario, Pedidos, Despacho)

## Puerto
`8083`

## Base de datos
`resena-db` (MySQL)

## Tecnologías
- Java 21
- Spring Boot 3.3.0
- Spring Data JPA + Hibernate
- Spring Cloud OpenFeign
- Flyway (migraciones SQL)
- Bean Validation (JSR 380)
- SLF4J (logs estructurados)
- Lombok
- MySQL

## Estructura del proyecto (Patrón CSR)
```
src/main/java/cl/jorge/resena/
├── client/              # Feign Clients (comunicación entre microservicios)
│   ├── ClienteClient.java
│   ├── ProductoClient.java
│   ├── PedidoClient.java
│   └── PagoClient.java
├── controller/          # Capa de presentación REST
│   └── ResenaController.java
├── dto/                 # Objetos de transferencia de datos
│   ├── ResenaRequest.java
│   ├── ResenaDTO.java
│   ├── ActualizarEstadoRequest.java
│   ├── RespuestaResenaRequest.java
│   ├── RespuestaResenaDTO.java
│   ├── ClienteResumenDTO.java
│   └── ProductoResumenDTO.java
├── exception/           # Manejo centralizado de errores
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── EstadoInvalidoException.java
├── model/               # Entidades JPA
│   ├── Resena.java
│   ├── RespuestaResena.java
│   └── EstadoResena.java (enum)
├── repository/          # Capa de persistencia JPA
│   ├── ResenaRepository.java
│   └── RespuestaResenaRepository.java
├── service/             # Lógica de negocio
│   └── ResenaService.java
└── ResenaApplication.java
```

## Funcionalidades implementadas
- CRUD completo de reseñas
- Sistema de moderación con estados (PENDIENTE → APROBADA / RECHAZADA)
- Respuestas oficiales a reseñas aprobadas
- Filtros por cliente, producto y estado
- Cálculo de promedio de calificaciones por producto
- Validaciones Bean Validation (JSR 380)
- Manejo centralizado de excepciones con @ControllerAdvice
- Logs estructurados con SLF4J en todas las capas
- Comunicación distribuida con Feign Clients:
  - Valida existencia de cliente (microservicio-cliente :8080)
  - Valida existencia de producto (microservicio-producto :8084)
  - Valida existencia de pedido (microservicio-pedidos :8081)
  - Consulta pagos del cliente (microservicio-pagos :8082)
- Endpoints de integración distribuida: resumen por producto y por cliente

## Pasos para ejecutar

### 1. Crear la base de datos
```sql
CREATE DATABASE `resena-db`;
```

### 2. Configurar credenciales
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

### 3. Compilar y ejecutar
```bash
mvn clean install
mvn spring-boot:run
```
El servidor arranca en `http://localhost:8083`

Flyway ejecuta automáticamente `V1__Create_Resena_Tables.sql` al iniciar.

## Endpoints principales

### Reseñas
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/v1/resenas` | Crear reseña |
| GET | `/api/v1/resenas` | Listar todas |
| GET | `/api/v1/resenas/{id}` | Obtener por ID |
| GET | `/api/v1/resenas/cliente/{clienteId}` | Reseñas de un cliente |
| GET | `/api/v1/resenas/producto/{productoId}` | Reseñas de un producto |
| GET | `/api/v1/resenas/producto/{productoId}/aprobadas` | Solo reseñas aprobadas (público) |
| GET | `/api/v1/resenas/estado/{estado}` | Filtrar por estado |
| PUT | `/api/v1/resenas/{id}` | Actualizar reseña (solo PENDIENTE) |
| PATCH | `/api/v1/resenas/{id}/estado` | Cambiar estado de moderación |
| DELETE | `/api/v1/resenas/{id}` | Eliminar reseña |

### Respuestas
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/v1/resenas/{resenaId}/respuestas` | Responder reseña aprobada |
| GET | `/api/v1/resenas/{resenaId}/respuestas` | Listar respuestas de una reseña |

### Integración distribuida
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/v1/resenas/producto/{productoId}/resumen` | Resumen + datos del producto (remoto) |
| GET | `/api/v1/resenas/cliente/{clienteId}/resumen` | Reseñas + datos del cliente (remoto) |

## Reglas de negocio
1. Un cliente solo puede reseñar el mismo producto una vez por pedido.
2. Toda reseña nueva inicia en estado `PENDIENTE`.
3. Solo se pueden editar reseñas en estado `PENDIENTE`.
4. Solo se pueden responder reseñas en estado `APROBADA`.
5. Las transiciones de estado siguen el diagrama: `PENDIENTE → APROBADA | RECHAZADA`, `APROBADA → RECHAZADA`.
6. Solo reseñas `APROBADAS` son visibles al público (endpoint `/aprobadas`).
