package com.duoc.pedidos.exception;

public class PedidosNotFoundException extends RuntimeException {
    public PedidosNotFoundException(Long id) {
        super("Pedido no encontrado con id" + id);
    }
}
