package duoc.cl.despacho.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Client — valida que el pedido existe en MS pedido (puerto 8084)
@FeignClient(name = "pedido-service", url = "${api.pedidos.url}")
public interface PedidoFeignClient {

    // Retorna Object genérico para no acoplar con entidades del MS pedido
    @GetMapping("/api/v1/pedidos/{id}")
    Object obtenerPedido(@PathVariable("id") Long id);
}