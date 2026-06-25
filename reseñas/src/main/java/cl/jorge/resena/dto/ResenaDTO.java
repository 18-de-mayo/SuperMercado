package cl.jorge.resena.dto;

import cl.jorge.resena.model.EstadoResena;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO que representa una reseña completa con respuestas")
public class ResenaDTO {
    @Schema(description = "ID único de la reseña", example = "1")
    private Long id;
    @Schema(description = "ID del cliente que realizó la reseña", example = "1")
    private Long clienteId;
    @Schema(description = "ID del producto reseñado", example = "100")
    private Long productoId;
    private Long pedidoId; // <-- Ya es un String
    @Schema(description = "Puntuación del 1 al 5", example = "4")
    private Integer calificacion;
    private String titulo;
    @Schema(description = "Texto del comentario", example = "Muy buen producto, lo recomiendo")
    private String comentario;
    @Schema(description = "Estado de la reseña (PENDIENTE, APROBADA, RECHAZADA)", example = "APROBADA")
    private EstadoResena estado;
    @Schema(description = "Fecha de creación de la reseña")
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;
    private List<RespuestaResenaDTO> respuestas;
}