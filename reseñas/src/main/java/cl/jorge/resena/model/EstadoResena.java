package cl.jorge.resena.model;

/**
 * Enum que define los estados válidos de una reseña en el sistema del supermercado.
 * IE 2.2.1: Centraliza las reglas de negocio de transición de estado.
 *
 * Diagrama de transiciones permitidas:
 *   PENDIENTE → APROBADA
 *   PENDIENTE → RECHAZADA
 *   APROBADA  → RECHAZADA  (moderación posterior)
 *   RECHAZADA → (terminal)
 */
public enum EstadoResena {
    PENDIENTE,
    APROBADA,
    RECHAZADA;

    /**
     * Valida si la transición desde el estado actual al nuevo estado es permitida
     * según las reglas de moderación del supermercado.
     *
     * @param nuevoEstado estado al que se desea transitar
     * @return true si la transición es válida
     */
    public boolean puedeTransicionarA(EstadoResena nuevoEstado) {
        return switch (this) {
            case PENDIENTE -> nuevoEstado == APROBADA || nuevoEstado == RECHAZADA;
            case APROBADA  -> nuevoEstado == RECHAZADA; // Se puede rechazar a posteriori
            case RECHAZADA -> false; // Estado terminal
        };
    }
}
