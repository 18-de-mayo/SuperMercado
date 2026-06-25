package com.duoc.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO de un detalle de pedido")
public class DetallePedidoDTO {
    @Schema(description = "ID único del detalle", example = "1")
    private Long id;
    @Schema(description = "ID del producto en el catálogo maestro", example = "100")
    private Long idProducto;
    @Schema(description = "Cantidad solicitada del producto", example = "2")
    private Integer cantidad;
    @Schema(description = "Precio unitario del producto al momento del pedido", example = "1500")
    private BigDecimal precioUnitario;
}
