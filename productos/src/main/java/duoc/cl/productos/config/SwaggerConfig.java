package duoc.cl.productos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI productoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Productos API")
                        .description("""
                                API REST para la gestión de productos del sistema.
                                Permite crear, listar, buscar por nombre, filtrar con stock y paginar resultados.
                                """)
                        .version("1.0.0"));
    }
}
