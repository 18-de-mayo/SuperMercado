package com.microservicio.cliente.exception;

/**
 * Excepción lanzada cuando se intenta una transición de estado inválida.
 * Por ejemplo, SUSPENDIDO → ACTIVO directamente.
 * Mapeada a HTTP 422 Unprocessable Entity por el GlobalExceptionHandler.
 */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String message) {
        super(message);
    }
}
