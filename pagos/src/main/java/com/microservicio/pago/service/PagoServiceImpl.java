package com.microservicio.pago.service;

import com.microservicio.pago.client.ClienteClient;
import com.microservicio.pago.client.PedidoClient;
import com.microservicio.pago.client.dto.PedidoResponseDTO;
import com.microservicio.pago.dto.PagoRequestDTO;
import com.microservicio.pago.dto.PagoResponseDTO;
import com.microservicio.pago.exception.EstadoPagoInvalidoException;
import com.microservicio.pago.exception.PagoNotFoundException;
import com.microservicio.pago.exception.PagoYaExisteException;
import com.microservicio.pago.model.Pago;
import com.microservicio.pago.model.Pago.EstadoPago;
import com.microservicio.pago.model.Pago.MetodoPago;
import com.microservicio.pago.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de pagos.
 *
 * Reglas de negocio implementadas:
 * 1. Un pedido solo puede tener un pago (restricción 1:1).
 * 2. El número de recibo se genera automáticamente con formato REC-YYYY-NNNNNN.
 * 3. Las transiciones de estado son unidireccionales y controladas.
 * 4. Solo se eliminan pagos en estado CANCELADO o FALLIDO.
 * 5. Al confirmar (COMPLETADO), se registra la fechaPago.
 */
@Slf4j
@Service
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoClient pedidoClient;
    private final ClienteClient clienteClient;

    public PagoServiceImpl(PagoRepository pagoRepository,
                           PedidoClient pedidoClient,
                           ClienteClient clienteClient) {
        this.pagoRepository = pagoRepository;
        this.pedidoClient = pedidoClient;
        this.clienteClient = clienteClient;
    }

    // ── Creación ─────────────────────────────────────────────────────────────

    @Override
    public PagoResponseDTO crearPago(PagoRequestDTO dto) {
        log.info("[PAGO] Iniciando creación de pago para pedidoId={}", dto.getPedidoId());

        // Regla de negocio: 1 pago por pedido
        if (pagoRepository.existsByPedidoId(dto.getPedidoId())) {
            log.warn("[PAGO] Pedido {} ya tiene un pago registrado", dto.getPedidoId());
            throw new PagoYaExisteException(dto.getPedidoId());
        }

        // Validación remota: el pedido debe existir
        PedidoResponseDTO pedido = pedidoClient.obtenerPedidoPorId(dto.getPedidoId());
        log.debug("[PAGO] Pedido obtenido: id={}, idCliente={}, estado={}",
                  pedido.getId(), pedido.getIdCliente(), pedido.getEstadoPedido());

        // Validación remota: el cliente debe estar activo
        Map<String, Boolean> respuestaCliente = clienteClient.clienteEstaActivo(pedido.getIdCliente());
        Boolean clienteActivo = respuestaCliente.getOrDefault("activo", false);
        if (!Boolean.TRUE.equals(clienteActivo)) {
            log.warn("[PAGO] Cliente {} no está activo, pago rechazado", pedido.getIdCliente());
            throw new EstadoPagoInvalidoException(
                    "No se puede crear el pago: el cliente con ID " +
                    pedido.getIdCliente() + " no está activo.");
        }

        Pago pago = Pago.builder()
                .pedidoId(dto.getPedidoId())
                .clienteId(pedido.getIdCliente())
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago())
                .estado(EstadoPago.PENDIENTE)
                .notas(dto.getNotas())
                .build();

        // Persistir primero para obtener el ID generado
        Pago guardado = pagoRepository.save(pago);

        // Generar el número de recibo con el ID real
        guardado.setNumeroRecibo(generarNumeroRecibo(guardado.getId()));
        guardado = pagoRepository.save(guardado);

        log.info("[PAGO] Pago creado exitosamente: id={}, recibo={}, pedidoId={}",
                 guardado.getId(), guardado.getNumeroRecibo(), guardado.getPedidoId());

        return mapToDTO(guardado);
    }

    // ── Consultas ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorId(Long id) {
        log.debug("[PAGO] Buscando pago con id={}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));
        return mapToDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorPedidoId(Long pedidoId) {
        log.debug("[PAGO] Buscando pago para pedidoId={}", pedidoId);
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new PagoNotFoundException(
                        "No se encontró pago para el pedido con ID: " + pedidoId));
        return mapToDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorNumeroRecibo(String numeroRecibo) {
        log.debug("[PAGO] Buscando pago con recibo={}", numeroRecibo);
        Pago pago = pagoRepository.findByNumeroRecibo(numeroRecibo)
                .orElseThrow(() -> new PagoNotFoundException(
                        "No se encontró pago con número de recibo: " + numeroRecibo));
        return mapToDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarTodos() {
        log.debug("[PAGO] Listando todos los pagos");
        return pagoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPorCliente(Long clienteId) {
        log.debug("[PAGO] Listando pagos del cliente id={}", clienteId);
        return pagoRepository.findByClienteId(clienteId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPorEstado(EstadoPago estado) {
        log.debug("[PAGO] Listando pagos con estado={}", estado);
        return pagoRepository.findByEstado(estado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarPorMetodoPago(MetodoPago metodoPago) {
        log.debug("[PAGO] Listando pagos con metodoPago={}", metodoPago);
        return pagoRepository.findByMetodoPago(metodoPago).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ── Cambios de estado ────────────────────────────────────────────────────

    @Override
    public PagoResponseDTO cambiarEstado(Long id, EstadoPago nuevoEstado) {
        log.info("[PAGO] Cambiando estado del pago id={} a {}", id, nuevoEstado);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));

        validarTransicionEstado(pago.getEstado(), nuevoEstado);

        pago.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPago.COMPLETADO) {
            pago.setFechaPago(LocalDateTime.now());
        }

        Pago actualizado = pagoRepository.save(pago);
        log.info("[PAGO] Estado actualizado: id={}, estadoAnterior={}, estadoNuevo={}",
                 id, pago.getEstado(), nuevoEstado);
        return mapToDTO(actualizado);
    }

    @Override
    public PagoResponseDTO confirmarPago(Long id) {
        return cambiarEstado(id, EstadoPago.COMPLETADO);
    }

    @Override
    public PagoResponseDTO cancelarPago(Long id) {
        return cambiarEstado(id, EstadoPago.CANCELADO);
    }

    @Override
    public PagoResponseDTO marcarComoFallido(Long id) {
        return cambiarEstado(id, EstadoPago.FALLIDO);
    }

    @Override
    public PagoResponseDTO reembolsarPago(Long id) {
        return cambiarEstado(id, EstadoPago.REEMBOLSADO);
    }

    // ── Reportes ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCompletados(LocalDateTime desde, LocalDateTime hasta) {
        log.debug("[PAGO] Calculando total completado entre {} y {}", desde, hasta);
        return pagoRepository.sumarMontosCompletadosEntreFechas(desde, hasta);
    }

    // ── Eliminación ──────────────────────────────────────────────────────────

    @Override
    public void eliminarPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));

        // Regla de negocio: solo se eliminan pagos no procesados
        if (pago.getEstado() != EstadoPago.CANCELADO && pago.getEstado() != EstadoPago.FALLIDO) {
            throw new EstadoPagoInvalidoException(
                    "Solo se pueden eliminar pagos en estado CANCELADO o FALLIDO. " +
                    "Estado actual: " + pago.getEstado());
        }

        pagoRepository.delete(pago);
        log.info("[PAGO] Pago eliminado: id={}, recibo={}", id, pago.getNumeroRecibo());
    }

    // ── Lógica de transiciones de estado ─────────────────────────────────────

    /**
     * Valida que la transición de estado sea permitida.
     *
     * Transiciones válidas:
     * - PENDIENTE   → COMPLETADO, FALLIDO, CANCELADO
     * - COMPLETADO  → REEMBOLSADO
     * - FALLIDO     → (ninguna)
     * - CANCELADO   → (ninguna)
     * - REEMBOLSADO → (ninguna)
     */
    private void validarTransicionEstado(EstadoPago estadoActual, EstadoPago estadoNuevo) {
        boolean valida = switch (estadoActual) {
            case PENDIENTE   -> estadoNuevo == EstadoPago.COMPLETADO
                                || estadoNuevo == EstadoPago.FALLIDO
                                || estadoNuevo == EstadoPago.CANCELADO;
            case COMPLETADO  -> estadoNuevo == EstadoPago.REEMBOLSADO;
            case FALLIDO,
                 CANCELADO,
                 REEMBOLSADO -> false;
        };

        if (!valida) {
            log.warn("[PAGO] Transición inválida: {} → {}", estadoActual, estadoNuevo);
            throw new EstadoPagoInvalidoException(estadoActual, estadoNuevo);
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    /**
     * Genera un número de recibo único con formato: REC-YYYY-NNNNNN.
     * Ejemplo: REC-2025-000042
     */
    private String generarNumeroRecibo(Long id) {
        String anio = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        return String.format("REC-%s-%06d", anio, id);
    }

    // ── Actualización ─────────────────────────────────────────────────────────

    @Override
    public PagoResponseDTO actualizarPago(Long id, PagoRequestDTO dto) {
        log.info("[PAGO] Actualizando pago id={}", id);
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));

        pago.setMetodoPago(dto.getMetodoPago());
        pago.setNotas(dto.getNotas());

        Pago actualizado = pagoRepository.save(pago);
        log.info("[PAGO] Pago actualizado: id={}", id);
        return mapToDTO(actualizado);
    }

    /** Mapea una entidad Pago a su DTO de respuesta. */
    PagoResponseDTO mapToDTO(Pago pago) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedidoId())
                .clienteId(pago.getClienteId())
                .numeroRecibo(pago.getNumeroRecibo())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .notas(pago.getNotas())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaActualizacion(pago.getFechaActualizacion())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}
