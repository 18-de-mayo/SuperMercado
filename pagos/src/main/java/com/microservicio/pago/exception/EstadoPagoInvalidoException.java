package com.microservicio.pago.exception;

import com.microservicio.pago.model.Pago.EstadoPago;

/**
 * Excepción lanzada cuando se intenta realizar una transición de estado
 * inválida sobre un pago (ej: COMPLETADO → PENDIENTE).
 */
public class EstadoPagoInvalidoException extends RuntimeException {
    public EstadoPagoInvalidoException(EstadoPago estadoActual, EstadoPago estadoNuevo) {
        super("Transición de estado inválida: no se puede pasar de " +
              estadoActual + " a " + estadoNuevo + ".");
    }
    public EstadoPagoInvalidoException(String message) {
        super(message);
    }
}
