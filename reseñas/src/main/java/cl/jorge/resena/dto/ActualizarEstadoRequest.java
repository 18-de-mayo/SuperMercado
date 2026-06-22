package cl.jorge.resena.dto;

import cl.jorge.resena.model.EstadoResena;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para cambiar el estado de moderación de una reseña.
 * IE 2.2.2: Validación Bean Validation sobre el campo de estado.
 */
@Data
@Schema(description = "Solicitud para actualizar el estado de una reseña")
public class ActualizarEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoResena nuevoEstado;
}
