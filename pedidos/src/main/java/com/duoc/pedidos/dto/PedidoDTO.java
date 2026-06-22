package com.duoc.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO que representa un pedido completo con sus detalles")
public class PedidoDTO {
    private Integer id;
    private Integer idCliente;
    private String estadoPedido;
    private String fechaPedido;
    private List<DetallePedidoDTO> detalles;
}
