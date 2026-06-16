package com.microservicio.pago.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para verificar que un cliente existe y está activo
 * en el microservicio de clientes antes de autorizar un pago.
 */
@FeignClient(name = "cliente-service", url = "${microservicios.clientes.url}")
public interface ClienteClient {

    /**
     * Verifica si un cliente está activo.
     *
     * @param id identificador del cliente
     * @return true si el cliente existe y está en estado ACTIVO
     */
    @GetMapping("/api/clientes/{id}/activo")
    Boolean clienteEstaActivo(@PathVariable("id") Long id);
}
