package com.duoc.pedidos.model;

/**
 * Estados posibles del ciclo de vida de un pedido.
 * <pre>
 *     PENDIENTE
 *        │
 *        ├──→ COMPLETADO  (pago confirmado / despacho generado)
 *        └──→ CANCELADO   (cliente anula o pago fallido)
 * </pre>
 * Cualquier transición distinta a las permitidas es rechazada
 * por la máquina de estados en {@code PedidosService}.
 */
public enum EstadoPedido {
    PENDIENTE,
    COMPLETADO,
    CANCELADO
}
