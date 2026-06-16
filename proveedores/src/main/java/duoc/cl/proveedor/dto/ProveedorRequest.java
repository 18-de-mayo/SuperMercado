package duoc.cl.proveedor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
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