package duoc.cl.despacho.exception;

// Excepción lanzada cuando no se encuentra un despacho por ID
public class DespachoNotFoundException extends RuntimeException {
    public DespachoNotFoundException(Long id) {
        super("No se encontró el despacho con ID: " + id);
    }
}