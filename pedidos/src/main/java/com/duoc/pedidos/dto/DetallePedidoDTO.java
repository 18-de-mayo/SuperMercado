package com.duoc.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de un detalle de pedido")
public class DetallePedidoDTO {
    private Integer id;
    private Integer idProducto;
    private Integer cantidad;
    private Integer precioUnitario;
}
