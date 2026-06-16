package com.microservicio.pago.exception;

/**
 * Excepción lanzada cuando se intenta crear un pago para un pedido
 * que ya tiene un pago asociado (violación de regla 1:1).
 */
public class PagoYaExisteException extends RuntimeException {
    public PagoYaExisteException(Long pedidoId) {
        super("El pedido con ID " + pedidoId + " ya tiene un pago registrado. " +
              "Solo se permite un pago por pedido.");
    }
}
