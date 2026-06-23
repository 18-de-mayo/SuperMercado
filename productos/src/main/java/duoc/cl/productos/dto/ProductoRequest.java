package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Estructura de datos requerida para crear o actualizar un Producto")
public class ProductoRequest {

    @NotBlank(message = "el nombre es obligatorio")
    @Schema(example = "Coca-Cola Original", description = "Nombre comercial del nuevo producto", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "la descripcion es obligatoria")
    @Schema(example = "Bebida fantasía sabor original 500 ml", description = "Detalles y especificaciones del producto", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descripcion;

    @Min(value = 1, message = "el precio debe ser mayor a 0")
    @Schema(example = "1200.50", description = "Precio unitario del producto, debe ser mayor a 0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precio;

    @Min(value = 0, message = "la cantidad no puede ser negativo")
    @Schema(example = "50", description = "Stock inicial disponible en bodega", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @NotNull(message = "el proveedor es obligatorio")
    @Schema(example = "2", description = "ID del proveedor previamente registrado", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long proveedorId;

    @NotBlank(message = "La categoría es obligatoria")
    @Schema(example = "Bebidas", description = "Nombre de la categoría a la que pertenece el producto", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoria;
}