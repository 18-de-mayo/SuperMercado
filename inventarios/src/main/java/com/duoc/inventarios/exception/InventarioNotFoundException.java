package com.duoc.inventarios.exception;

public class InventarioNotFoundException extends RuntimeException {
    public InventarioNotFoundException(Integer id) {
        super("No existe el inventario con el id: " + id);
    }
}
