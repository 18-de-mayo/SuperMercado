package duoc.cl.despacho.model;

/**
 * Estados del ciclo de vida de un despacho.
 * <pre>
 *     PENDIENTE  →  EN_RUTA  →  ENTREGADO
 * </pre>
 * Las transiciones son unidireccionales y secuenciales.
 * No se permiten retrocesos ni saltos de estado.
 */
public enum EstadoDespacho {
    PENDIENTE,
    EN_RUTA,
    ENTREGADO
}
