package com.microservicio.pago.service;

import com.microservicio.pago.dto.PagoRequestDTO;
import com.microservicio.pago.dto.PagoResponseDTO;
import com.microservicio.pago.model.Pago.EstadoPago;
import com.microservicio.pago.model.Pago.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de la capa de servicio para la gestión de pagos.
 */
public interface PagoService {

    /** Crea un nuevo pago. Valida la regla 1 pago por pedido. */
    PagoResponseDTO crearPago(PagoRequestDTO dto);

    /** Obtiene un pago por su ID. */
    PagoResponseDTO obtenerPorId(Long id);

    /** Obtiene el pago asociado a un pedido específico. */
    PagoResponseDTO obtenerPorPedidoId(Long pedidoId);

    /** Obtiene un pago por su número de recibo. */
    PagoResponseDTO obtenerPorNumeroRecibo(String numeroRecibo);

    /** Lista todos los pagos registrados. */
    List<PagoResponseDTO> listarTodos();

    /** Lista los pagos de un cliente específico. */
    List<PagoResponseDTO> listarPorCliente(Long clienteId);

    /** Lista los pagos filtrados por estado. */
    List<PagoResponseDTO> listarPorEstado(EstadoPago estado);

    /** Lista los pagos filtrados por método de pago. */
    List<PagoResponseDTO> listarPorMetodoPago(MetodoPago metodoPago);

    /**
     * Cambia el estado de un pago respetando las transiciones válidas:
     * - PENDIENTE → COMPLETADO, FALLIDO, CANCELADO
     * - COMPLETADO → REEMBOLSADO
     * - FALLIDO, CANCELADO, REEMBOLSADO → sin transición posible
     */
    PagoResponseDTO cambiarEstado(Long id, EstadoPago nuevoEstado);

    /** Confirma un pago (PENDIENTE → COMPLETADO). Establece fechaPago. */
    PagoResponseDTO confirmarPago(Long id);

    /** Cancela un pago (PENDIENTE → CANCELADO). */
    PagoResponseDTO cancelarPago(Long id);

    /** Registra el fallo de un pago (PENDIENTE → FALLIDO). */
    PagoResponseDTO marcarComoFallido(Long id);

    /** Reembolsa un pago (COMPLETADO → REEMBOLSADO). */
    PagoResponseDTO reembolsarPago(Long id);

    /** Retorna el monto total de pagos COMPLETADOS entre dos fechas. */
    BigDecimal calcularTotalCompletados(LocalDateTime desde, LocalDateTime hasta);

    /** Elimina un pago. Solo permitido si está en estado CANCELADO o FALLIDO. */
    void eliminarPago(Long id);

    /** Actualiza los campos editables de un pago (notas, método de pago). */
    PagoResponseDTO actualizarPago(Long id, PagoRequestDTO dto);
}
