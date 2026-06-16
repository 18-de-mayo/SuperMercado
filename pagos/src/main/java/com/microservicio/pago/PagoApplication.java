package com.microservicio.pago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada del microservicio de pagos.
 * Gestiona todos los pagos del supermercado, validando pedidos y clientes
 * mediante comunicación REST con otros microservicios.
 */
@SpringBootApplication
@EnableFeignClients
public class PagoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PagoApplication.class, args);
    }
}
