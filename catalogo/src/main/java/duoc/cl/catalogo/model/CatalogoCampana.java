package duoc.cl.catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList; // Importante
import java.util.List;

@Entity
@Table(name = "catalogo_campanas")
@Data
@Schema(description = "Entidad que representa una campaña promocional")
public class CatalogoCampana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_campana", nullable = false, length = 100)
    private String nombreCampana;

    // Inicializamos la lista aquí mismo para evitar NullPointerException e incompatibilidades de JPA
    @OneToMany(mappedBy = "campana", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CatalogoItem> items = new ArrayList<>();
}
