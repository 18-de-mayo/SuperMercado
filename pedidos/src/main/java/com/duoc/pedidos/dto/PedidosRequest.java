package com.duoc.pedidos.dto;

import com.duoc.pedidos.model.EstadoPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Solicitud para crear o actualizar un pedido")
public class PedidosRequest {

    private Integer idCliente;

    @NotNull(message = "La fecha de pedido es obligatoria")
    private LocalDateTime fechaPedido;

    @NotNull(message = "El estado de pedido es obligatorio")
    private EstadoPedido estadoPedido;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<DetallePedidosRequest> detalles;
}
