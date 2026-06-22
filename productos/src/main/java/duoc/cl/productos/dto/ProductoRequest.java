package duoc.cl.productos.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Solicitud para crear o actualizar un producto")
public class ProductoRequest {

    @NotBlank(message = "el nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "la descripcion es obligatoria")
    private String descripcion;

    @DecimalMin(value = "0.01", message = "el precio debe ser mayor a 0")
    @NotNull(message = "el precio es obligatorio")
    private BigDecimal precio;

    @Min(value = 0, message = "la cantidad no puede ser negativo")
    @NotNull(message = "la cantidad es obligatoria")
    private Integer cantidad;

    @NotNull(message = "el proveedor es obligatorio")
    private Long proveedorId;
}
