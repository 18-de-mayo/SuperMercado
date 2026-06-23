package duoc.cl.productos.client;

import duoc.cl.productos.dto.CategoriaDTO;
import duoc.cl.productos.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-categoria")
public interface CategoriaClient {

    @GetMapping("/api/v1/categorias/{id}")
    CategoriaDTO buscarPorId(@PathVariable("id") Long id);
}