package duoc.cl.despacho.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// DTO que mapea la respuesta del MS proveedor
@Data
@Schema(description = "DTO con datos básicos del proveedor")
public class ProveedorDTO {
    private Long id;
    private String nombre;
}