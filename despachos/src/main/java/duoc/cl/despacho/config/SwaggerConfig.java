package duoc.cl.despacho.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8089}")
    private String serverPort;

    @Bean
    public OpenAPI despachoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Despachos API")
                        .description("""
                                API REST para la gestión de despachos del sistema.
                                Permite registrar envíos, listar, consultar por ID y actualizar estados.
                                """)
                        .version("1.0.0"));
    }
}
