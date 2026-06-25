package duoc.cl.proveedor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proveedores API")
                        .version("1.0.0")
                        .description("API para la gestión de proveedores del supermercado. Permite crear, listar, buscar, actualizar y eliminar proveedores del sistema.")
                        .contact(new Contact()
                                .name("Desarrollo")
                                .email("desarrollo@supermercado.cl")));
    }
}
