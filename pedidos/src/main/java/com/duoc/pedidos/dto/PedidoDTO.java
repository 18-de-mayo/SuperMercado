package com.duoc.pedidos.dto;

import com.duoc.pedidos.model.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO que representa un pedido completo con sus detalles")
public class PedidoDTO {
    @Schema(description = "ID único del pedido", example = "1")
    private Long id;
    @Schema(description = "ID del cliente que realizó el pedido", example = "1")
    private Long idCliente;
    @Schema(description = "Estado actual del pedido", example = "PENDIENTE")
    private EstadoPedido estadoPedido;
    @Schema(description = "Fecha de creación del pedido")
    private LocalDateTime fechaPedido;
    @Schema(description = "Lista de productos incluidos en el pedido")
    private List<DetallePedidoDTO> detalles;
}
