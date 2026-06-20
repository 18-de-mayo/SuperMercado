package com.duoc.inventarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Punto de entrada del MS inventario
// @EnableFeignClients activa la comunicación con MS producto
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class InventariosApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventariosApplication.class, args);
	}
}