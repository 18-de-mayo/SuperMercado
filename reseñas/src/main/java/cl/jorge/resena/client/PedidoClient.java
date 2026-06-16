package cl.jorge.resena.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pedido-service", url = "${api.pedidos.url}")
public interface PedidoClient {

    @GetMapping("/buscar/{numeroPedido}")
    Object obtenerPedidoPorId(@PathVariable("numeroPedido") Long numeroPedido);
}