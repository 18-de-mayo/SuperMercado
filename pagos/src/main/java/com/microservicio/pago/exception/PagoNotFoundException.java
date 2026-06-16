package com.microservicio.pago.exception;

/**
 * Excepción lanzada cuando no se encuentra un pago con el ID o criterio dado.
 */
public class PagoNotFoundException extends RuntimeException {
    public PagoNotFoundException(String message) {
        super(message);
    }
    public PagoNotFoundException(Long id) {
        super("No se encontró ningún pago con ID: " + id);
    }
}
