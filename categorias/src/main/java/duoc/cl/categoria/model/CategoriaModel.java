package duoc.cl.categoria.model;

import jakarta.persistence.*;
import lombok.Data;

// Entidad JPA — representa la tabla 'categorias' en la base de datos
@Data
@Entity
@Table(name = "categorias")
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la categoría — obligatorio y único
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;
}