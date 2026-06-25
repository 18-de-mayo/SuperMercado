package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoDTO {
    @Schema(example = "1", description = "id inmremental")
    private Long id;

    @Schema(example = "coca-cola original", description = "nombre del producto")
    private String nombre;

    @Schema(example = "Bebida fantasia sabor original 500 ml", description = "descipcion del producto")
    private String descripcion;

    @Schema(example = "1200.50", description = "precio unitario del producto")
    private BigDecimal precio;

    @Schema(example = "10", description = "cantidad de unidades del producto")
    private Integer cantidad;

    @Schema(example = "coca cola", description = "marca o proveedor del producto")
    private String nombreProveedor;

    @Schema(example = "1", description = "ID de la categoría (referencia al microservicio categorias)")
    private Long categoriaId;

    @Schema(example = "Bebidas", description = "Nombre de la categoría obtenido desde el microservicio remoto")
    private String nombreCategoria;

}