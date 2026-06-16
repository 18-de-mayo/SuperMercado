package com.microservicio.pago.client;

import com.microservicio.pago.client.dto.PedidoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicación REST con el microservicio de pedidos.
 * Permite validar que un pedido existe y obtener su cliente asociado
 * antes de procesar un pago.
 */
@FeignClient(name = "pedido-service", url = "${microservicios.pedidos.url}")
public interface PedidoClient {

    /**
     * Obtiene los datos de un pedido por su ID.
     *
     * @param id identificador del pedido
     * @return DTO con los datos del pedido (incluye clienteId y total)
     */
    @GetMapping("/api/pedidos/{id}")
    PedidoResponseDTO obtenerPedidoPorId(@PathVariable("id") Long id);
}
