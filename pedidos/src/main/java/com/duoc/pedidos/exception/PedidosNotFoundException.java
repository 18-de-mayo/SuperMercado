package com.duoc.pedidos.exception;

public class PedidosNotFoundException extends RuntimeException {
    public PedidosNotFoundException(Integer id) {
        super("Pedido no encontrado con id" + id);
    }
}
