package duoc.cl.categoria.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Habilita el patrón de diseño Builder, vital para el DataLoader/DataFaker
@Schema(description = "Entidad de persistencia que representa la tabla de categorías en la base de datos relacional")
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Clave primaria autoincremental", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Nombre único e indexado de la categoría", example = "Congelados")
    private String nombre;

    @Column(length = 255)
    @Schema(description = "Detalle descriptivo de los productos de la categoría", example = "Platos preparados, verduras y mariscos congelados")
    private String descripcion;
}