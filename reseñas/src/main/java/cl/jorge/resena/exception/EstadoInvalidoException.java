package cl.jorge.resena.exception;

/**
 * Excepción lanzada cuando se intenta realizar una transición de estado inválida.
 * IE 2.2.1: Protege las reglas de negocio de moderación de reseñas.
 * Ejemplo: intentar cambiar el estado de una reseña RECHAZADA (estado terminal).
 */
public class EstadoInvalidoException extends RuntimeException {
    public EstadoInvalidoException(String message) {
        super(message);
    }
}
