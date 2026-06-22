package duoc.cl.proveedor.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedor")
@Schema(description = "Entidad que representa un proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nombre de la empresa
    private String nombre;

    // rut unico para evitar duplicados
    @Column(unique = true)
    private String rut;

    private String correo;

    private String direccion;

    private String telefono;
}