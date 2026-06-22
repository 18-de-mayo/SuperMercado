package duoc.cl.proveedor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear o actualizar un proveedor")
public class ProveedorRequest {

    @NotBlank(message = "el nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "el rut es obligatorio")
    private String rut;

    @Email(message = "correo invalido")
    private String correo;

    @NotBlank(message = "la direccion es obligatoria")
    private String direccion;

    @NotBlank(message = "el telefono es obligatorio")
    private String telefono;
}