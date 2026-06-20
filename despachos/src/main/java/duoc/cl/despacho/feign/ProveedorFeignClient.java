package duoc.cl.despacho.feign;

import duoc.cl.despacho.dto.ProveedorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Client — obtiene datos del proveedor desde MS proveedor (puerto 8082)
@FeignClient(name = "proveedor-service", url = "${api.proveedores.url}")
public interface ProveedorFeignClient {

    @GetMapping("/api/v1/proveedor/{id}")
    ProveedorDTO obtenerProveedor(@PathVariable("id") Long id);
}