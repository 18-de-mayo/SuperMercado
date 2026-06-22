package duoc.cl.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gateway")
                        .description("""
                                API Gateway que centraliza el enrutamiento hacia todos los microservicios.
                                Proporciona circuit breakers, reintentos y un endpoint de fallback unificado.
                                """)
                        .version("1.0.0"));
    }
}
