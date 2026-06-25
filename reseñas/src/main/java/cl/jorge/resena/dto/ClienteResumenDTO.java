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
    @Schema(description = "ID del cliente", example = "1")
    private Long id;
    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String nombre;
    @Schema(description = "RUT del cliente", example = "12345678-9")
    private String rut;
}
