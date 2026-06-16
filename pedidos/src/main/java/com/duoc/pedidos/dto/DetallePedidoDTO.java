package com.duoc.pedidos.dto;

import lombok.Data;

@Data
public class DetallePedidoDTO {
    private Integer id;
    private Integer idProducto;
    private Integer Cantidad;
    private Integer precioUnitario;
}
