package duoc.cl.proveedor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Estructura de datos requerida para dar de alta o modificar un Proveedor")
public class ProveedorRequest {

    @NotBlank(message = "el nombre es obligatorio")
    @Schema(example = "Distribuidora Central S.A.", description = "Nombre o razón social del proveedor", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "el rut es obligatorio")
    @Schema(example = "76123456-9", description = "RUT comercial sin puntos, con guión y DV", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rut;

    @Email(message = "correo invalido")
    @NotBlank(message = "el correo es obligatorio") // Opcional: agregarlo si consideras que no debe ir vacío
    @Schema(example = "contacto@distribuidoracentral.cl", description = "Email corporativo para órdenes de compra", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;

    @NotBlank(message = "la direccion es obligatoria")
    @Schema(example = "Av. Vitacura 1234, Oficina 501, Santiago", description = "Dirección de despacho/comercial", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccion;

    @NotBlank(message = "el telefono es obligatorio")
    @Schema(example = "+56912345678", description = "Teléfono de red fija o celular", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;
}