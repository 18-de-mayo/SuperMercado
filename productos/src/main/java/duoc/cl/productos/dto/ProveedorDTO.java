package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Información básica simplificada del proveedor del producto")
public class ProveedorDTO {

    @Schema(example = "5", description = "ID único del proveedor en su microservicio")
    private Long id;

    @Schema(example = "Coca-Cola Company", description = "Nombre comercial del proveedor")
    private String nombre;
}