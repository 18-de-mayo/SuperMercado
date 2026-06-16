package com.duoc.pedidos.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class PedidoDTO {
    private Integer id;
    private Integer idCliente;
    private String estadoPedido;
    private String fechaPedido;
    private List<DetallePedidoDTO> detalles;
}
