package cl.jorge.resena.client;

import cl.jorge.resena.dto.ClienteResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client para comunicarse con el microservicio de Clientes (puerto 8080).
 * IE 2.4.1: Consume endpoint remoto para validar que el cliente existe antes
 * de permitir que publique una reseña.
 */
@FeignClient(name = "cliente-service", url = "${api.clientes.url}")
public interface ClienteClient {

    /**
     * Verifica que el cliente existe en el sistema.
     * Usado en la capa de servicio para validación de negocio antes de crear una reseña.
     */
    @GetMapping("/api/v1/clientes/{id}")
    ClienteResumenDTO obtenerClientePorId(@PathVariable("id") Long id);
}
