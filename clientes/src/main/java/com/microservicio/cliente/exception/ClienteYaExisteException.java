package com.microservicio.cliente.exception;

/**
 * Excepción lanzada cuando se intenta registrar un cliente con email o RUT duplicado.
 * Mapeada a HTTP 409 Conflict por el GlobalExceptionHandler.
 */
public class ClienteYaExisteException extends RuntimeException {
    public ClienteYaExisteException(String message) {
        super(message);
    }
}
