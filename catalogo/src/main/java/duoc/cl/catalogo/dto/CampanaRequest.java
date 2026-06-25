package duoc.cl.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear o actualizar una campaña")
public class CampanaRequest {
    @NotBlank(message = "El nombre de la campaña es obligatorio")
    @Schema(description = "Nombre de la campaña", example = "Ofertas de Verano")
    private String nombre;
}
