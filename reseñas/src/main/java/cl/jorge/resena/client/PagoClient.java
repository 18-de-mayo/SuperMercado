package cl.jorge.resena.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client para comunicarse con el microservicio de Pagos (puerto 8082).
 * IE 2.4.1: Permite al microservicio de Reseñas consultar el historial de
 * pagos de un cliente para enriquecer reportes o validar compras pagadas.
 */
@FeignClient(name = "pago-service", url = "${api.pagos.url}")
public interface PagoClient {

    /**
     * Obtiene los pagos asociados a un cliente.
     * Retorna Object genérico para evitar acoplamiento con entidades externas.
     */
    @GetMapping("/api/v1/pagos/clientes/{clienteId}")
    Object obtenerPagosPorCliente(@PathVariable("clienteId") Long clienteId);
}
