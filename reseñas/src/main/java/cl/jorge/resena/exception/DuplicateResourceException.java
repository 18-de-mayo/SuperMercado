package cl.jorge.resena.exception;

/**
 * Excepción lanzada cuando se intenta registrar un recurso duplicado.
 * Ejemplo: un cliente que intenta reseñar el mismo producto del mismo pedido dos veces.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
