package cl.jorge.resena.dto;

import cl.jorge.resena.model.EstadoResena;
import lombok.Data; // <-- Asegúrate de que esta línea esté presente
import java.time.LocalDateTime;
import java.util.List;

@Data
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