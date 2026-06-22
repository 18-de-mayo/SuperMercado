package com.duoc.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Solicitud para crear o actualizar un pedido")
public class PedidosRequest {

    private Integer idCliente;

    @NotBlank(message = "La fecha de pedido no puede estar vacia")
    private String fechaPedido;

    @NotBlank(message = "El estado de pedido no puede estar vacio")
    private String estadoPedido;

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<DetallePedidosRequest> detalles;
}
