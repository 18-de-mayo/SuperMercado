package cl.jorge.resena.dto;

import cl.jorge.resena.model.EstadoResena;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO que representa una reseña completa con respuestas")
public class ResenaDTO {
    private Long id;
    private Long clienteId;
    private Long productoId;
    private Long pedidoId; // <-- Ya es un String
    private Integer calificacion;
    private String titulo;
    private String comentario;
    private EstadoResena estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;
    private List<RespuestaResenaDTO> respuestas;
}