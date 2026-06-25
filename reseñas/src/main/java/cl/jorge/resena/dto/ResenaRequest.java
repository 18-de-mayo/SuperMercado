package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Solicitud para crear o actualizar una reseña")
public class ResenaRequest {

    @Schema(description = "ID del cliente que realiza la reseña", example = "1")
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @Schema(description = "ID del producto a reseñar", example = "100")
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    // CORREGIDO: Cambiado de Long a String y removido @Positive
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @Schema(description = "Puntuación del 1 al 5", example = "4")
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    private String titulo;
    @Schema(description = "Comentario de la reseña", example = "Muy buen producto, lo recomiendo")
    private String comentario;
}