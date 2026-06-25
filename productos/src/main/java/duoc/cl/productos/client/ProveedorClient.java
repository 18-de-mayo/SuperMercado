package duoc.cl.productos.client;

import duoc.cl.productos.dto.ProveedorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "proveedor-service")
public interface ProveedorClient {

    @GetMapping("/api/v1/proveedores/{id}")
    ProveedorDTO obtenerProveedor(@PathVariable Long id);
}