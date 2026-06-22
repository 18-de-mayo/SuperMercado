package duoc.cl.catalogo.feign;

import duoc.cl.catalogo.dto.ProductoDTO;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "producto-service",
        url = "${api.productos.url}"
)
public interface ProductoFeignClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoDTO buscarProducto(
            @PathVariable Long id
    );

}