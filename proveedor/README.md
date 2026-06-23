# Microservicio de Proveedores (ms-proveedor)

Este microservicio fue desarrollado por **Gonzalo Martínez** y forma parte del ecosistema distribuido del proyecto, siendo **uno de los 10 microservicios independientes** diseñados y construidos de forma modular para dar soporte integral a la plataforma transaccional de la tienda.

Su propósito central es el gobierno y administración del maestro de proveedores, el control de la información fiscal (RUT), la gestión de canales de contacto de los distribuidores y la auditoría de registros comerciales. Actúa de forma integrada con el clúster de la arquitectura, permitiendo que otros componentes verifiquen dinámicamente los datos de despacho y origen de las mercancías.

---

## 🚀 Arquitectura y Componentes Tecnológicos

La solución ha sido construida siguiendo los más altos estándares modernos de backend para sistemas distribuidos, integrándose con los otros 9 componentes del ecosistema de la tienda:

* **Autor:** Gonzalo Martínez (go.martinezs@duocuc.cl).
* **Contexto:** Componente central dentro de una arquitectura mallada de 10 microservicios.
* **Lenguaje de Programación:** Java 21 (aprovechando características de colecciones secuenciales para aserciones).
* **Framework Base:** Spring Boot 3.x (Spring Web, Spring Data JPA).
* **Gestión de Base de Datos:** Motor relacional MySQL.
* **Evolución del Esquema:** Flyway Core para la ejecución automatizada y versionada de scripts migratorios SQL.
* **Comunicación Inter-servicio:** Spring Cloud OpenFeign habilitado preventivamente para interoperabilidad síncrona.
* **Descubrimiento de Servicios:** Netflix Eureka Client para la auto-matriculación y localización dinámica de rutas.
* **Documentación de API:** SpringDoc OpenAPI 3 (Swagger UI) para la auto-generación de contratos de servicios interactivos.
* **Suite de Pruebas:** JUnit 5 y Mockito Extension para pruebas unitarias y aislamiento estricto de dependencias.

---

## ⚙️ Configuración del Sistema (`application.properties`)

El comportamiento del entorno se gestiona de manera centralizada en el archivo de propiedades. Se destaca la desconexión del mapeador automático de Hibernate (`ddl-auto=none`) para evitar colisiones con los scripts estructurados de Flyway, garantizando la predictibilidad y estabilidad del esquema SQL.

```properties
spring.application.name=ms-proveedor
server.port=${PORT:8082}

# Parámetros de Conexión a Base de Datos (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/proveedor?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root

# Configuración de JPA (Control estricto e histórico delegado a Flyway)
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Inclusión de mensajes detallados en excepciones
spring.web.error.include-message=always
spring.web.error.include-binding-errors=always

# Localización de Scripts de Migración (Flyway)
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# Servidor de Descubrimiento (Eureka Registry)
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Configuración de Rutas de Swagger UI
springdoc.swagger-ui.path=/doc/swagger-ui.html

📡 Interoperabilidad y Registro Distribuido
En cumplimiento con el diseño de sistemas distribuidos, ms-proveedor opera de manera completamente desacoplada de la red física. Durante su arranque, el microservicio utiliza su cliente de Eureka para auto-matricularse en el servidor de descubrimiento bajo el nombre lógico de ms-proveedor.

Esto permite que componentes como el ms-producto o el API Gateway localicen su ubicación física (IP y Puerto dinámico) mediante simples consultas de nombre, facilitando la tolerancia a fallos y la elasticidad del clúster sin quemar direcciones estáticas (hardcodeadas) en el código.

📑 Catálogo de Endpoints de la API
La API expone sus operaciones de CRUD y consultas bajo el prefijo unificado /api/v1/proveedores. A continuación se detallan los servicios disponibles:

POST /api/v1/proveedores : Registra un nuevo proveedor en el sistema. Realiza validaciones estrictas de campos obligatorios. Si el correo o el RUT ya existen, interrumpe el flujo y devuelve un estado 400 Bad Request.

GET /api/v1/proveedores : Recupera el listado completo de los distribuidores registrados, mapeando de forma íntegra todos sus atributos hacia estructuras ProveedorDTO.

GET /api/v1/proveedores/{id} : Busca los datos detallados de un proveedor específico mediante su clave primaria. Si la consulta falla, propaga una excepción controlada 404 Not Found.

PUT /api/v1/proveedores/{id} : Actualiza de manera segura las propiedades de un proveedor existente, verificando de forma previa que los datos modificados (RUT/Correo) no pertenezcan a otro registro.

DELETE /api/v1/proveedores/{id} : Elimina físicamente el registro de la base de datos si el identificador coincide con un registro real.

Consola de Pruebas OpenAPI: Con el microservicio en ejecución, es posible interactuar directamente con los métodos y revisar los esquemas de transferencia JSON detallados accediendo a la URL: http://localhost:8082/doc/swagger-ui.html

🧪 Estrategia de Pruebas Unitarias (Validación Verde)
La capa de negocio (ProveedorService) cuenta con una suite automatizada robusta que supera con éxito el 80% de cobertura de código exigido. Las pruebas se rigen bajo el patrón de diseño Given-When-Then utilizando mocks inyectados de manera limpia:

Se aísla por completo el acceso físico al repositorio de datos simulando las respuestas de la base de datos.

Se validan los caminos felices de persistencia y actualización asignando el mapeo íntegro de propiedades.

Se simula y evalúa la robustez del sistema provocando colisiones intencionales de RUT y Correo Electrónico duplicados, comprobando que el microservicio responda con las excepciones de negocio adecuadas.

Se incorporaron aserciones nativas adaptadas a las características de Java 21 (.getFirst()) para asegurar el orden y consistencia de las colecciones devueltas.

Para ejecutar la suite de pruebas desde la terminal del sistema:

Bash
./mvnw clean test

