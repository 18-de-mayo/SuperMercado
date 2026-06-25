package duoc.cl.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Punto de entrada del MS categoria
// No usa Feign — es un dominio base sin dependencias de otros MS
@EnableFeignClients(basePackages = "duoc.cl.catalogo.client")
@EnableDiscoveryClient
@SpringBootApplication
public class CatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogoApplication.class, args);
	}
}