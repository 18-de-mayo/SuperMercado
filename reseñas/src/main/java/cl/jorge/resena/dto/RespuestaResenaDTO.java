package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de salida para el recurso RespuestaResena.
 * IE 1.2.1: Separación entre representación externa y entidad JPA.
 */
@Data
@Schema(description = "DTO de una respuesta a una reseña")
public class RespuestaResenaDTO {
    @Schema(description = "ID único de la respuesta", example = "1")
    private Long id;
    @Schema(description = "ID de la reseña asociada", example = "1")
    private Long resenaId;
    @Schema(description = "Nombre del autor de la respuesta", example = "Servicio al Cliente")
    private String autor;
    @Schema(description = "Contenido de la respuesta", example = "Gracias por su comentario")
    private String contenido;
    @Schema(description = "Fecha de creación de la respuesta")
    private LocalDateTime fechaCreacion;
}
