package com.duoc.pedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI pedidosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pedidos API")
                        .description("""
                                API REST para la gestión de pedidos del sistema.
                                Permite crear, listar, buscar, actualizar y eliminar pedidos con sus detalles.
                                """)
                        .version("1.0.0"));
    }
}
