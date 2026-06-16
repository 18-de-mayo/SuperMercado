package com.microservicio.pago.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de Swagger/OpenAPI para el microservicio de pagos.
 * Acceder a la documentación en: http://localhost:8085/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pago Microservicio API")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión de pagos del supermercado.
                                
                                **Reglas de negocio principales:**
                                - Cada pedido puede tener como máximo un pago (restricción 1:1).
                                - El número de recibo se genera automáticamente (formato: REC-YYYY-NNNNNN).
                                - El cliente debe estar activo para crear un pago.
                                - Las transiciones de estado son unidireccionales y controladas.
                                """)
                        .contact(new Contact()
                                .name("Equipo Supermercado")
                                .email("dev@supermercado.cl")))
                .servers(List.of(
                        new Server().url("http://localhost:8085").description("Servidor local (dev)"),
                        new Server().url("https://pago-service.railway.app").description("Servidor remoto (prod)")
                ));
    }
}
