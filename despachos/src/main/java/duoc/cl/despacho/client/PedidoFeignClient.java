package duoc.cl.despacho.client;

import duoc.cl.despacho.dto.PedidoResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Client — valida que el pedido existe en MS pedido (puerto 8084)
@FeignClient(name = "pedido-service", url = "${api.pedidos.url}")
public interface PedidoFeignClient {

    @GetMapping("/api/v1/pedidos/{id}")
    PedidoResumenDTO obtenerPedido(@PathVariable("id") Long id);
}