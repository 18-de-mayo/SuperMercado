package duoc.cl.catalogo.dto;

import lombok.Data;

@Data
public class CatalogoItemDTO {

    private Long id;
    private Long productoId;
    private String nombreProducto;
    private String descripcion;
    private String nombreProveedor;
    private Double precioCatalogo;
    private Double precioOferta;
    private String estadoStock;
}