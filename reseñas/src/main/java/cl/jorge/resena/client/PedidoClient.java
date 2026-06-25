package cl.jorge.resena.client;

import cl.jorge.resena.dto.PedidoResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pedido-service", url = "${api.pedidos.url}")
public interface PedidoClient {

    @GetMapping("/api/v1/pedidos/{id}")
    PedidoResumenDTO obtenerPedidoPorId(@PathVariable("id") Long id);
}