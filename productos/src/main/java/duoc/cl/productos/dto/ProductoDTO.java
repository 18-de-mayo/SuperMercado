package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa un producto")
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private String nombreProveedor;

}