package com.duoc.inventarios.client;

import com.duoc.inventarios.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Client — valida que el producto existe en el MS producto (puerto 8081)
@FeignClient(name = "producto-service", url = "${api.productos.url}")
public interface ProductoClient {

    // Busca un producto por ID — más eficiente que traer la lista completa
    @GetMapping("/api/v1/productos/{id}")
    ProductoDTO obtenerProductoPorId(@PathVariable("id") Long id);
}