package duoc.cl.proveedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa un proveedor")
public class ProveedorDTO {

    private Long id;

    private String nombre;

}
