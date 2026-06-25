package cl.jorge.resena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Punto de entrada del MS reseña
// @EnableFeignClients activa la comunicación con otros microservicios
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ResenaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResenaApplication.class, args);
    }
}