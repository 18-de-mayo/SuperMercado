package duoc.cl.categoria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8090}")
    private String serverPort;

    @Bean
    public OpenAPI categoriaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Categorias API")
                        .description("""
                                API REST para la gestión de categorías del sistema.
                                Permite crear, listar, buscar, actualizar y eliminar categorías.
                                """)
                        .version("1.0.0"));
    }
}
