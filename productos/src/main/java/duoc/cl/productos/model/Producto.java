package duoc.cl.productos.model;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private Double precio;

    private Integer cantidad;

    @Column(name = "proveedor_id")
    private Long proveedorId;
}