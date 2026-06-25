package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para crear una respuesta a una reseña existente.
 * IE 2.2.2: Validaciones Bean Validation aplicadas sobre el DTO.
 */
@Data
@Schema(description = "Solicitud para agregar una respuesta a una reseña")
public class RespuestaResenaRequest {

    @Schema(description = "Nombre del autor de la respuesta", example = "Servicio al Cliente")
    @NotBlank(message = "El nombre del autor es obligatorio")
    @Size(max = 100, message = "El autor no puede superar los 100 caracteres")
    private String autor;

    @Schema(description = "Contenido de la respuesta", example = "Gracias por su comentario")
    @NotBlank(message = "El contenido de la respuesta es obligatorio")
    @Size(min = 5, max = 1000, message = "El contenido debe tener entre 5 y 1000 caracteres")
    private String contenido;
}
