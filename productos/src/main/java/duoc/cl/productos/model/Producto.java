package duoc.cl.productos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entidad JPA que representa un producto en el catálogo maestro del supermercado.
 * Cada producto pertenece a una categoría (por nombre) y a un proveedor (por ID lógico),
 * siguiendo el diseño de microservicios donde las relaciones cross-service se mantienen
 * como referencias lógicas sin FK reales.
 */
@Entity
@Table(name = "productos")
@Data
@Schema(description = "Entidad que representa un producto en la base de datos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre comercial del producto", example = "Coca-Cola Original")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Descripción detallada del producto", example = "Bebida fantasia sabor original 500 ml")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "Precio unitario actual del producto", example = "1200.50")
    private BigDecimal precio;

    @Column(nullable = false)
    @Schema(description = "Stock disponible en bodega central", example = "50")
    private Integer cantidad;

    @Column(name = "proveedor_id", nullable = false)
    @Schema(description = "ID del proveedor (referencia lógica al microservicio proveedor)", example = "2")
    private Long proveedorId;

    @Column(name = "categoria_id", nullable = false)
    @Schema(description = "ID de la categoría (referencia lógica al microservicio categorias)", example = "1")
    private Long categoriaId;
}