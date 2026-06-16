package duoc.cl.productos.exception;

public class ProductoDuplicadoException extends RuntimeException {

    public ProductoDuplicadoException(String nombre){

        super("el producto ya existe: " + nombre);
    }
}