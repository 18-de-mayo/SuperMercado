package duoc.cl.catalogo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList; // Importante
import java.util.List;

@Entity
@Table(name = "catalogo_campanas")
@Data
public class CatalogoCampana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCampana;

    // Inicializamos la lista aquí mismo para evitar NullPointerException e incompatibilidades de JPA
    @OneToMany(mappedBy = "campana", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CatalogoItem> items = new ArrayList<>();
}

//prueba Gonzalo-Model