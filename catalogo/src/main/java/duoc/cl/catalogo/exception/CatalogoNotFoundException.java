package duoc.cl.catalogo.exception;

public class CatalogoNotFoundException
        extends RuntimeException {

    public CatalogoNotFoundException(Long id) {

        super("catalogo no encontrado con id: " + id);

    }

}