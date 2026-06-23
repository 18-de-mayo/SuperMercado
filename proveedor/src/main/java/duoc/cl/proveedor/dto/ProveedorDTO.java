package duoc.cl.proveedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Modelo que expone los datos completos de un Proveedor")
public class ProveedorDTO {

    @Schema(example = "1", description = "ID autoincremental del proveedor", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(example = "Distribuidora Central S.A.", description = "Nombre o razón social de la empresa")
    private String nombre;

    @Schema(example = "76123456-9", description = "RUT de la empresa con guión y dígito verificador")
    private String rut;

    @Schema(example = "contacto@distribuidoracentral.cl", description = "Correo electrónico de contacto")
    private String correo;

    @Schema(example = "Av. Los Toros 1234, Oficina 501, Santiago", description = "Dirección física de la casa matriz")
    private String direccion;

    @Schema(example = "+56912345678", description = "Teléfono de contacto con código de país")
    private String telefono;
}