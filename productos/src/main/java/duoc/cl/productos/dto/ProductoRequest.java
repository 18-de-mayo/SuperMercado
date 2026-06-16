package duoc.cl.productos.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class ProductoRequest {

    // no permite texto vacio
    @NotBlank(message = "el nombre es obligatorio")
    private String nombre;

    // descripcion obligatoria
    @NotBlank(message = "la descripcion es obligatoria")
    private String descripcion;

    // precio minimo
    @Min(value = 1, message = "el precio debe ser mayor a 0")
    private Double precio;

    // stock no puede ser negativo
    @Min(value = 0, message = "la cantidad no puede ser negativo")
    private Integer cantidad;

    //pedir id del proveedor creado
    @NotNull(message = "el proveedor es obligatorio")
    private Long proveedorId;
}