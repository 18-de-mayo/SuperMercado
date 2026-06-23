package duoc.cl.productos.exception;

public class ProductoDuplicadoException extends RuntimeException {
    public ProductoDuplicadoException(String nombre) {
        super("El producto '" + nombre + "' ya existe en el sistema.");
    }
}