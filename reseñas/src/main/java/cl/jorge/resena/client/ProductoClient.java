package cl.jorge.resena.client;

import cl.jorge.resena.dto.ProductoResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Client — valida que el producto existe antes de registrar una reseña
// Producto corre en puerto 8081
@FeignClient(name = "producto-service", url = "${api.productos.url}")
public interface ProductoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoResumenDTO obtenerProductoPorId(@PathVariable("id") Long id);
}