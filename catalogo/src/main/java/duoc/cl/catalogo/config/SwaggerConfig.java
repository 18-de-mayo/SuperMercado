package duoc.cl.catalogo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI catalogoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalogo API")
                        .description("""
                                API REST para la gestión de catálogos y campañas promocionales.
                                Permite crear campañas, agregar productos con precios especiales y consultar catálogos.
                                """)
                        .version("1.0.0"));
    }
}
