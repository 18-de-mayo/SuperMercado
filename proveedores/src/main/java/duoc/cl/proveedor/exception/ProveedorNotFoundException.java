package duoc.cl.proveedor.exception;

// error personalizado cuando no existe proveedor
public class ProveedorNotFoundException extends RuntimeException {

    public ProveedorNotFoundException(Long id) {
        super("no existe proveedor con id: " + id);
    }
}