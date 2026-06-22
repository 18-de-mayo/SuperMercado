package com.microservicio.cliente.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger / OpenAPI para la documentación del microservicio.
 * Accesible en: http://localhost:8086/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8086}")
    private String serverPort;

    @Bean
    public OpenAPI clienteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio Cliente API")
                        .description("""
                                API REST para la gestión de clientes del sistema de e-commerce.
                                
                                ## Funcionalidades:
                                - Registro y actualización de clientes
                                - Búsqueda por ID, email y RUT
                                - Filtrado por estado (ACTIVO / INACTIVO / SUSPENDIDO)
                                - Verificación de estado para uso por otros microservicios
                                
                                ## Reglas de negocio:
                                - El email y RUT son únicos por cliente
                                - Un cliente SUSPENDIDO no puede pasar directamente a ACTIVO
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev@microservicios.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor local (dev)"),
                        new Server()
                                .url("https://cliente-service.railway.app")
                                .description("Servidor remoto (prod)")
                ));
    }
}
