package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para recibir datos remotos del microservicio de Productos.
 * IE 2.4.1: Mapea la respuesta del Feign Client del servicio de productos.
 */
@Data
@Schema(description = "DTO con resumen de producto para reseñas")
public class ProductoResumenDTO {
    private Long id;
    private String nombre;
    private Double precio;
}
