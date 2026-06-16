package cl.jorge.resena.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso en la base de datos.
 * Extiende RuntimeException para integrarse con @ControllerAdvice sin checked exceptions.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
