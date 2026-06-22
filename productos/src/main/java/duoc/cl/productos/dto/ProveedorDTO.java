package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO con datos básicos del proveedor")
public class ProveedorDTO {

    private Long id;
    private String nombre;
}