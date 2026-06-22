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
    private Long id;
    private Long resenaId;
    private String autor;
    private String contenido;
    private LocalDateTime fechaCreacion;
}
