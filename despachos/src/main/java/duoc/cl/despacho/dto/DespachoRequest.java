package duoc.cl.despacho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO de entrada — valida los datos al crear un despacho
@Data
public class DespachoRequest {

    // ID del pedido que origina el despacho — se valida que exista en MS pedido
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    // ID del proveedor que despacha — se usa para mostrar nombre en respuesta
    @NotNull(message = "El ID del proveedor es obligatorio")
    private Long proveedorId;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;
}