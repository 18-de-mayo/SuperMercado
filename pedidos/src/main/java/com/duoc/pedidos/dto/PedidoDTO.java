package com.duoc.pedidos.dto;

import com.duoc.pedidos.model.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO que representa un pedido completo con sus detalles")
public class PedidoDTO {
    private Integer id;
    private Integer idCliente;
    private EstadoPedido estadoPedido;
    private LocalDateTime fechaPedido;
    private List<DetallePedidoDTO> detalles;
}
