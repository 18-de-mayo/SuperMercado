package com.microservicio.pago.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

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
     * @return mapa con la clave "activo" indicando si el cliente está activo
     */
    @GetMapping("/api/v1/clientes/{id}/activo")
    Map<String, Boolean> clienteEstaActivo(@PathVariable("id") Long id);
}
