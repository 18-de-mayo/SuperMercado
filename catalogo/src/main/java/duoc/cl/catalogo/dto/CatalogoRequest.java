package duoc.cl.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

import lombok.Data;

@Data
@Schema(description = "Solicitud para agregar un producto al catálogo")
public class CatalogoRequest {

    // producto obligatorio
    @NotNull(message = "el producto es obligatorio")
    @Schema(description = "ID del producto")
    private Long productoId;

    // precio catalogo obligatorio
    @Positive(message = "precio debe ser mayor a cero")
    @NotNull(message = "el precio catalogo es obligatorio")
    @Schema(description = "Precio en catálogo", example = "19990")
    private BigDecimal precioCatalogo;

    // precio oferta obligatorio
    @Positive(message = "precio debe ser mayor a cero")
    @NotNull(message = "el precio oferta es obligatorio")
    @Schema(description = "Precio de oferta", example = "15990")
    private BigDecimal precioOferta;

}