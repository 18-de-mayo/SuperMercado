package duoc.cl.catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "catalogo_items")
@Data
@Schema(description = "Entidad que representa un item dentro de una campaña")
public class CatalogoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productoId;
    private Double precioCatalogo;
    private Double precioOferta;

    @ManyToOne
    @JoinColumn(name = "campana_id")
    private CatalogoCampana campana; // Relación con el papá (Invierno/Verano)
}