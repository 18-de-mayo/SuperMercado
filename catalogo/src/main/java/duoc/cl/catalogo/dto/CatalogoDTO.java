package duoc.cl.catalogo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CatalogoDTO {

    private Long id;
    private String nombreProveedor;
    private String nombreProducto;
    private String descripcion;
    private Double precioCatalogo;
    private Double precioOferta;
    private String estadoStock;
}