package duoc.cl.productos.exception;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(Long id){

        super("producto no encontrado con id: " + id);
    }
}