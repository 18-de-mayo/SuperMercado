package com.duoc.pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
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
