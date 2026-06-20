package duoc.cl.despacho.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Entidad JPA — representa la tabla 'despachos' en la base de datos
@Data
@Entity
@Table(name = "despachos")
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del pedido en MS pedido — referencia lógica, no FK de BD
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    // ID del proveedor en MS proveedor — se usa para consultar nombre via Feign
    @Column(name = "proveedor_id", nullable = false)
    private Long proveedorId;

    @Column(name = "direccion_destino", nullable = false)
    private String direccionDestino;

    @Column(nullable = false)
    private String comuna;

    // Estado del despacho: PENDIENTE → EN_RUTA → ENTREGADO
    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Se ejecuta automáticamente antes de persistir por primera vez
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
    }
}