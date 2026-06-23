package duoc.cl.proveedor.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedor")
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