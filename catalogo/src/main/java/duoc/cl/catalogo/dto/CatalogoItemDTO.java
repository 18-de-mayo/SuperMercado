package duoc.cl.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de un item dentro de una campaña del catálogo")
public class CatalogoItemDTO {

    @Schema(description = "ID del item")
    private Long id;
    @Schema(description = "ID del producto")
    private Long productoId;
    @Schema(description = "Nombre del producto")
    private String nombreProducto;
    @Schema(description = "Descripción del producto")
    private String descripcion;
    @Schema(description = "Nombre del proveedor")
    private String nombreProveedor;
    @Schema(description = "Precio en catálogo")
    private Double precioCatalogo;
    @Schema(description = "Precio de oferta")
    private Double precioOferta;
    @Schema(description = "Estado del stock")
    private String estadoStock;
}