package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO con resumen de pedido para reseñas (obtenido vía Feign desde MS pedidos)")
public class PedidoResumenDTO {
    @Schema(description = "ID del pedido", example = "1")
    private Long id;

    @Schema(description = "ID del cliente asociado", example = "1")
    private Long idCliente;

    @Schema(description = "Estado actual del pedido", example = "COMPLETADO")
    private String estadoPedido;
}
