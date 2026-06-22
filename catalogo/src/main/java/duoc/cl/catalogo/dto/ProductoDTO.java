package duoc.cl.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para datos básicos de un producto")
public class ProductoDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    private Integer cantidad;

    private String nombreProveedor;

}