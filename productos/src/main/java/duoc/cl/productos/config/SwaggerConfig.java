package duoc.cl.productos.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Microservicio Productos - Duoc UC")
                        .version("1.0.0")
                        .description("Micro servicio Productos - Duoc UC")
                        .contact(new Contact()
                                .name("Gonzalo Martinez")
                                .email("go.martinezs@duocuc.cl"))
                        .license(new License()
                                .name("DUOC UC - Fullstack I")
                                .url("https://www.duoc.cl")));
    }
}
