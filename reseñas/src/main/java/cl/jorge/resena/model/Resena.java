package cl.jorge.resena.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private Long productoId;

    // CORREGIDO: De Long a String
    private Long pedidoId;

    private Integer calificacion;
    private String titulo;
    private String comentario;

    @Enumerated(EnumType.STRING)
    private EstadoResena estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEdicion;

    @OneToMany(mappedBy = "resena", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaResena> respuestas;
}