package duoc.cl.despacho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Punto de entrada del MS despacho
// @EnableFeignClients activa la comunicación con MS pedido y MS proveedor
@EnableFeignClients
@SpringBootApplication
public class DespachoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DespachoApplication.class, args);
	}
}