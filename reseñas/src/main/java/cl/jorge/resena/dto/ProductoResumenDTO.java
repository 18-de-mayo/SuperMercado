package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para recibir datos remotos del microservicio de Productos.
 * IE 2.4.1: Mapea la respuesta del Feign Client del servicio de productos.
 */
@Data
@Schema(description = "DTO con resumen de producto para reseñas")
public class ProductoResumenDTO {
    @Schema(description = "ID del producto", example = "100")
    private Long id;
    @Schema(description = "Nombre del producto", example = "Coca-Cola Original")
    private String nombre;
    @Schema(description = "Precio del producto", example = "1200")
    private BigDecimal precio;
}
