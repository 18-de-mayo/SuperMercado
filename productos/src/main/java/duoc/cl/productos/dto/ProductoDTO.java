package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductoDTO {
    @Schema(example = "1", description = "id inmremental")
    private Long id;

    @Schema(example = "coca-cola original", description = "nombre del producto")
    private String nombre;

    @Schema(example = "Bebida fantasia sabor original 500 ml", description = "descipcion del producto")
    private String descripcion;

    @Schema(example = "10", description = "cantidad de unidades del producto")
    private Integer cantidad;

    @Schema(example = "coca cola", description = "marca o proveedor del producto")
    private String nombreProveedor;

    @Schema(example = "bebidas", description = "categoria del producto, familia de productos")
    private String categoria;

}