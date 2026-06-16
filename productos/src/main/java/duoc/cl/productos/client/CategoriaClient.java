package duoc.cl.productos.client;

import duoc.cl.productos.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "categoria-service", url = "http://localhost:8090")
public interface CategoriaClient {

    @GetMapping("api/v1/categorias/{categoria}")
    List<ProductoDTO> productosDeLaCategoria(@PathVariable String categoria);
}
