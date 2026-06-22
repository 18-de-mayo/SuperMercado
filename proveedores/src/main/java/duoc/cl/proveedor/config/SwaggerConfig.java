package duoc.cl.proveedor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI proveedorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proveedores API")
                        .description("""
                                API REST para la gestión de proveedores del sistema.
                                Permite listar, buscar, crear, actualizar y eliminar proveedores.
                                """)
                        .version("1.0.0"));
    }
}
