package duoc.cl.catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "catalogo_items")
@Data
@Schema(description = "Entidad que representa un item dentro de una campaña")
public class CatalogoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "precio_catalogo", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCatalogo;

    @Column(name = "precio_oferta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioOferta;

    @ManyToOne
    @JoinColumn(name = "campana_id")
    private CatalogoCampana campana;
}