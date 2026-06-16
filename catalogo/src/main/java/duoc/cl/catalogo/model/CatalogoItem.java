package duoc.cl.catalogo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "catalogo_items")
@Data
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