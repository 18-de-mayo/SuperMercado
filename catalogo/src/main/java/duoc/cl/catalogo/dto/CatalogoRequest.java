package duoc.cl.catalogo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class CatalogoRequest {

    // producto obligatorio
    @NotNull(message = "el producto es obligatorio")
    private Long productoId;

    // precio catalogo obligatorio
    @Positive(message = "precio debe ser mayor a cero")
    @NotNull(message = "el precio catalogo es obligatorio")
    private Double precioCatalogo;

    // precio oferta obligatorio
    @Positive(message = "precio debe ser mayor a cero")
    @NotNull(message = "el precio oferta es obligatorio")
    private Double precioOferta;

}