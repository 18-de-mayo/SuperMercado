package cl.jorge.resena.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8087}")
    private String serverPort;

    @Bean
    public OpenAPI resenaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Resenas API")
                        .description("""
                                API REST para la gestión de reseñas y valoraciones del sistema.
                                Permite crear, listar, buscar, moderar y responder reseñas de productos.
                                """)
                        .version("1.0.0"));
    }
}
