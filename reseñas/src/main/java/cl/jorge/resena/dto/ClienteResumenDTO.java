package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para recibir datos remotos del microservicio de Clientes.
 * IE 2.4.1: Mapea la respuesta del Feign Client del servicio de clientes.
 */
@Data
@Schema(description = "DTO con resumen de cliente para reseñas")
public class ClienteResumenDTO {
    private Long id;
    private String nombre;
    private String rut;
    private Double nivel;
}
