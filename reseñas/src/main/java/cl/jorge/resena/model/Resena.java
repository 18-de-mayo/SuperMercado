package cl.jorge.resena.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "resenas")
@Schema(description = "Entidad que representa una reseña de producto")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    @NotNull
    private Long clienteId;

    @Column(name = "producto_id", nullable = false)
    @NotNull
    private Long productoId;

    @Column(name = "pedido_id", nullable = false)
    @NotNull
    private Long pedidoId;

    @Column(nullable = false)
    @Min(1) @Max(5)
    @NotNull
    private Integer calificacion;

    @Column(length = 200)
    @Size(max = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @Size(max = 2000)
    private String comentario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private EstadoResena estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_edicion")
    private LocalDateTime fechaEdicion;

    @OneToMany(mappedBy = "resena", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaResena> respuestas;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoResena.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaEdicion = LocalDateTime.now();
    }
}