package duoc.cl.productos.client;

import duoc.cl.productos.dto.ProveedorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "proveedor-service", url = "${api.proveedores.url}")// y en application.properties:api.proveedores.url=http://localhost:8082)
public interface ProveedorClient {

    @GetMapping("/api/v1/proveedor/{id}")
    ProveedorDTO obtenerProveedor(@PathVariable Long id);
}