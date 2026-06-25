package com.duoc.pedidos.exception;

public class ClientesNotFoundException extends RuntimeException {
    public ClientesNotFoundException(Long id) {
        super("Cliente no encontrado con id" + id);
    }
}
