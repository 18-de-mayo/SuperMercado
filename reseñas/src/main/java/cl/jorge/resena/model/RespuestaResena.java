package cl.jorge.resena.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla 'respuestas_resena'.
 * IE 2.1.1 & IE 2.2.3: Entidad secundaria con relación ManyToOne hacia Resena.
 * Permite que el equipo del supermercado responda oficialmente una reseña.
 */
@Entity
@Table(name = "respuestas_resena")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una respuesta a una reseña")
public class RespuestaResena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * IE 2.2.3: Relación ManyToOne con Resena (FK local en BD).
     * Muchas respuestas pueden pertenecer a la misma reseña.
     * ON DELETE CASCADE garantizado a nivel de BD por la FK definida en migración SQL.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resena_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_respuestas_resena"))
    private Resena resena;

    /** Nombre del respondedor (ej: "Servicio al Cliente", "Gerencia"). */
    @Column(nullable = false, length = 100)
    private String autor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
