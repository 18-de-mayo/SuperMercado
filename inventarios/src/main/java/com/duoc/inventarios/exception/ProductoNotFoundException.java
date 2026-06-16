package com.duoc.inventarios.exception;

// Excepción lanzada cuando el producto no existe en el MS producto
// o cuando el MS producto no está disponible
public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(Integer id) {
        super("Producto no encontrado o MS producto no disponible. ID: " + id);
    }
}