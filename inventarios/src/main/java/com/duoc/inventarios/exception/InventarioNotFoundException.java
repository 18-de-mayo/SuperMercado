package com.duoc.inventarios.exception;

public class InventarioNotFoundException extends RuntimeException {
    public InventarioNotFoundException(Long id) {
        super("No existe el inventario con el id: " + id);
    }
}
