package duoc.cl.categoria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Punto de entrada del MS categoria
// No usa Feign — es un dominio base sin dependencias de otros MS
@EnableFeignClients
@SpringBootApplication
public class CategoriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CategoriaApplication.class, args);
	}
}