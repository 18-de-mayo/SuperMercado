package duoc.cl.proveedor.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa un proveedor registrado en el sistema.
 * Los proveedores son referenciados por otros microservicios (productos, despachos)
 * mediante su ID como referencia lógica, sin FK reales entre bases de datos.
 */
@Data
@Entity
@Table(name = "proveedor")
@Schema(description = "Entidad que representa un proveedor en la base de datos")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del proveedor", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre o razón social del proveedor", example = "Distribuidora Central S.A.")
    private String nombre;

    @Column(unique = true, nullable = false)
    @Schema(description = "RUT comercial sin puntos, con guión y DV", example = "76123456-9")
    private String rut;

    @Column(nullable = false)
    @Schema(description = "Email corporativo para órdenes de compra", example = "contacto@distribuidoracentral.cl")
    private String correo;

    @Column(nullable = false)
    @Schema(description = "Dirección comercial o de despacho del proveedor", example = "Av. Vitacura 1234, Santiago")
    private String direccion;

    @Column(nullable = false)
    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;
}