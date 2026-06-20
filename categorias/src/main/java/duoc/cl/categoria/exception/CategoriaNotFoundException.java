package duoc.cl.categoria.exception;

// Excepción lanzada cuando no se encuentra una categoría por ID
public class CategoriaNotFoundException extends RuntimeException {

    public CategoriaNotFoundException(Long id) {
        super("No se encontró la categoría con ID: " + id);
    }
}