# Microservicio de Productos (ms-producto)

Este microservicio fue desarrollado por **Gonzalo Martínez** y forma parte del ecosistema distribuido del proyecto, siendo **uno de los 10 microservicios independientes** diseñados y construidos para dar soporte integral a la plataforma de la tienda.

Su propósito central es la gestión del catálogo de productos, control de inventario y almacenamiento de especificaciones comerciales. Para consolidar los datos de cara al cliente final, el servicio interactúa de manera transparente y desacoplada con componentes remotos de la arquitectura a través de mecanismos de descubrimiento y comunicación declarativa.

---

## 🚀 Arquitectura y Componentes Tecnológicos

La solución ha sido construida siguiendo los estándares modernos de desarrollo backend para sistemas distribuidos, integrándose con los otros 9 componentes del ecosistema de la tienda:

* **Autor:** Gonzalo Martínez (go.martinezs@duocuc.cl).
* **Contexto:** Componente central dentro de una arquitectura mallada de 10 microservicios.
* **Lenguaje de Programación:** Java 21 (aprovechando características de colecciones secuenciales).
* **Framework Base:** Spring Boot 3.x (Spring Web, Spring Data JPA).
* **Gestión de Base de Datos:** Motor relacional MySQL.
* **Evolución del Esquema:** Flyway Core para el control de versiones migratorias del esquema SQL de forma automatizada.
* **Comunicación Inter-servicio:** Spring Cloud OpenFeign para peticiones síncronas HTTP declarativas.
* **Descubrimiento de Servicios:** Netflix Eureka Client para la auto-matriculación y localización dinámica de rutas.
* **Documentación de API:** SpringDoc OpenAPI 3 (Swagger UI) para la auto-generación de contratos de servicios.
* **Suite de Pruebas:** JUnit 5 y Mockito Extension para pruebas unitarias y aislamiento de dependencias.

---

## ⚙️ Configuración del Sistema (`application.properties`)

El comportamiento del entorno se gestiona de manera centralizada en el archivo de propiedades. Se destaca la desconexión del mapeador automático de Hibernate (`ddl-auto=none`) para evitar colisiones con los scripts estructurados de Flyway, garantizando la predictibilidad de la persistencia.

```properties
spring.application.name=ms-producto
server.port=${PORT:8081}

# Parámetros de Conexión a Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/db_productos?createDatabaseIfNotExist=true&serverTimezone=UTC&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA (Control estricto delegado a Flyway)
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Localización de Scripts de Migración (Flyway)
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration/productos

# Servidor de Descubrimiento (Eureka)
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Configuración de Rutas de Swagger UI
springdoc.swagger-ui.path=/doc/swagger-ui.html

📡 Interoperabilidad y Tolerancia a Fallos
El microservicio no requiere conocer las direcciones físicas (IP/Puertos) de los servicios de los cuales depende. Delega dicha resolución al servidor Eureka mediante consultas lógicas:

ms-categoria: Utilizado por CategoriaClient para verificar la existencia de clasificaciones y familias de productos en su respectivo dominio.

proveedor-service: Utilizado por ProveedorClient para adjuntar los datos comerciales válidos del distribuidor al dar de alta un artículo.

Resiliencia y Mitigación de Errores
La capa de negocio (ProductoService) implementa estructuras defensivas mediante bloques try-catch preparados para interceptar excepciones de comunicación de Feign. Si un servicio externo experimenta latencia o indisponibilidad completa, el microservicio captura la falla y responde al cliente con un estado 502 Bad Gateway, evitando la propagación de errores en cascada y protegiendo la estabilidad global de la tienda.

📑 Catálogo de Endpoints de la API
La API expone sus recursos bajo el prefijo unificado /api/v1/productos. A continuación se detallan las operaciones disponibles:

POST /api/v1/productos : Inserta un nuevo producto. Realiza validaciones estrictas en el cuerpo de la petición. Si el nombre ya existe localmente, interrumpe el flujo devolviendo un estado 409 Conflict.

GET /api/v1/productos : Obtiene el listado completo de los artículos en catálogo mapeados a estructuras DTO limpias.

GET /api/v1/productos/{id} : Recupera la información de un producto específico mediante su ID. Ante fallos de búsqueda, propaga un error 404 Not Found.

PUT /api/v1/productos/{id} : Modifica las propiedades de un artículo existente, verificando restricciones de nombres duplicados y consistencia de datos de manera previa.

DELETE /api/v1/productos/{id} : Borra físicamente el registro del sistema si el identificador coincide con un registro real en la base de datos.

GET /api/v1/productos/buscar : Realiza búsquedas parciales parametrizadas por texto (nombre), omitiendo discrepancias entre mayúsculas y minúsculas.

GET /api/v1/productos/stock : Filtra y recupera en una lista optimizada aquellos productos cuyo inventario actual sea estrictamente superior a cero.

GET /api/v1/productos/categoria/{categoriaId} : Endpoint de integración distribuida. Consulta al servicio remoto de categorías la validez del ID y retorna la colección local de productos asociados a dicha clasificación.

Interfaz Gráfica de Pruebas: Con el microservicio en ejecución, es posible interactuar con la consola de Swagger y revisar los esquemas JSON detallados accediendo a la URL: http://localhost:8081/doc/swagger-ui.html

🧪 Estrategia de Pruebas Unitarias
La lógica del servicio cuenta con una cobertura de pruebas automatizadas superior al 80% de las líneas de código, asegurando la estabilidad ante refactorizaciones futuras. Las pruebas se rigen bajo el patrón de diseño Given-When-Then utilizando mocks inyectados de manera limpia:

Se aíslan por completo los accesos a la base de datos y los clientes HTTP de Feign.

Se validan los caminos felices de persistencia y actualización mapeando respuestas compuestas de DTOs remotos.

Se simulan caídas de red e indisponibilidad de microservicios externos mediante lanzamientos controlados de excepciones (.thenThrow()) para verificar la correcta respuesta de Bad Gateway.

Se incorporaron aserciones modernas adaptadas a Java 21 (.getFirst()) para asegurar el orden y consistencia en el procesamiento de listas secuenciales.

Para ejecutar la suite de pruebas desde la terminal del sistema:

Bash
./mvnw clean test
🐋 Construcción y Despliegue con Docker
El proyecto incorpora un archivo Dockerfile optimizado mediante una estrategia de compilación en múltiples etapas (Multi-stage build). Esto permite generar un artefacto de producción sumamente liviano al separar las herramientas de compilación pesadas (JDK) del entorno de ejecución final (JRE).

1. Construcción de la Imagen
Ejecutar el siguiente comando situándose en el directorio raíz del proyecto (donde se ubica el archivo Dockerfile):

Bash
docker build -t ms-producto:1.0 .
2. Ejecución del Contenedor
Para instanciar el contenedor en segundo plano, publicando su puerto de escucha local en concordancia con las configuraciones del ecosistema distribuido, ejecutar:

Bash
docker run -d -p 8081:8081 --name ms-producto ms-producto:1.0