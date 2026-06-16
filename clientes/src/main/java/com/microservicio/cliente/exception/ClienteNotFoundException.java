package com.microservicio.cliente.exception;

/**
 * Excepción lanzada cuando no se encuentra un cliente con el ID o criterio dado.
 * Mapeada a HTTP 404 por el GlobalExceptionHandler.
 */
public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(String message) {
        super(message);
    }
}
