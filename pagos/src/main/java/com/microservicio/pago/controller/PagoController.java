package com.microservicio.pago.controller;

import com.microservicio.pago.dto.EstadoPagoRequestDTO;
import com.microservicio.pago.dto.PagoRequestDTO;
import com.microservicio.pago.dto.PagoResponseDTO;
import com.microservicio.pago.model.Pago.EstadoPago;
import com.microservicio.pago.model.Pago.MetodoPago;
import com.microservicio.pago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de pagos del supermercado.
 * Expone endpoints CRUD y operaciones de negocio sobre los pagos.
 */
@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "API para la gestión de pagos del supermercado")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    // ── POST: Crear pago ─────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Registrar un nuevo pago",
               description = "Crea un pago para un pedido. Valida que el pedido no tenga pago previo y que el cliente esté activo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente",
                         content = @Content(schema = @Schema(implementation = PagoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pedido/cliente no existe"),
            @ApiResponse(responseCode = "409", description = "El pedido ya tiene un pago registrado"),
            @ApiResponse(responseCode = "422", description = "Cliente inactivo, pago no permitido")
    })
    public ResponseEntity<PagoResponseDTO> crearPago(@Valid @RequestBody PagoRequestDTO dto) {
        log.info("[PAGO] POST /api/pagos - pedidoId={}", dto.getPedidoId());
        PagoResponseDTO respuesta = pagoService.crearPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // ── GET: Consultas ───────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar todos los pagos",
               description = "Retorna la lista completa de pagos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de pagos retornada")
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        log.info("[PAGO] GET /api/pagos - listando todos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID",
               description = "Busca y retorna un pago específico por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PagoResponseDTO> obtenerPorId(
            @Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        log.info("[PAGO] GET /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener pago por ID de pedido",
               description = "Retorna el pago asociado a un pedido específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe pago para ese pedido")
    })
    public ResponseEntity<PagoResponseDTO> obtenerPorPedidoId(
            @Parameter(description = "ID del pedido", example = "10") @PathVariable Long pedidoId) {
        log.info("[PAGO] GET /api/pagos/pedido/{}", pedidoId);
        return ResponseEntity.ok(pagoService.obtenerPorPedidoId(pedidoId));
    }

    @GetMapping("/recibo/{numeroRecibo}")
    @Operation(summary = "Obtener pago por número de recibo",
               description = "Busca un pago usando su número de recibo único (ej: REC-2025-000001).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Recibo no encontrado")
    })
    public ResponseEntity<PagoResponseDTO> obtenerPorRecibo(
            @Parameter(description = "Número de recibo", example = "REC-2025-000001")
            @PathVariable String numeroRecibo) {
        log.info("[PAGO] GET /api/pagos/recibo/{}", numeroRecibo);
        return ResponseEntity.ok(pagoService.obtenerPorNumeroRecibo(numeroRecibo));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pagos por cliente",
               description = "Retorna todos los pagos realizados por un cliente específico.")
    @ApiResponse(responseCode = "200", description = "Lista de pagos del cliente")
    public ResponseEntity<List<PagoResponseDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente", example = "3") @PathVariable Long clienteId) {
        log.info("[PAGO] GET /api/pagos/cliente/{}", clienteId);
        return ResponseEntity.ok(pagoService.listarPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar pagos por estado",
               description = "Filtra los pagos por su estado actual (PENDIENTE, COMPLETADO, FALLIDO, CANCELADO, REEMBOLSADO).")
    @ApiResponse(responseCode = "200", description = "Lista de pagos filtrados por estado")
    public ResponseEntity<List<PagoResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado del pago", example = "COMPLETADO")
            @PathVariable EstadoPago estado) {
        log.info("[PAGO] GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    @GetMapping("/metodo/{metodoPago}")
    @Operation(summary = "Listar pagos por método de pago",
               description = "Filtra los pagos por el método usado (EFECTIVO, TARJETA_DEBITO, etc.).")
    @ApiResponse(responseCode = "200", description = "Lista de pagos por método")
    public ResponseEntity<List<PagoResponseDTO>> listarPorMetodo(
            @Parameter(description = "Método de pago", example = "TARJETA_DEBITO")
            @PathVariable MetodoPago metodoPago) {
        log.info("[PAGO] GET /api/pagos/metodo/{}", metodoPago);
        return ResponseEntity.ok(pagoService.listarPorMetodoPago(metodoPago));
    }

    // ── PUT: Actualizar pago ─────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un pago",
               description = "Actualiza los campos editables de un pago (notas, método de pago). No permite cambiar estado ni monto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago actualizado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<PagoResponseDTO> actualizarPago(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO dto) {
        log.info("[PAGO] PUT /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.actualizarPago(id, dto));
    }

    // ── PATCH: Cambios de estado ─────────────────────────────────────────────

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de un pago",
               description = "Actualiza el estado de un pago respetando el flujo: PENDIENTE→COMPLETADO/FALLIDO/CANCELADO, COMPLETADO→REEMBOLSADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    public ResponseEntity<PagoResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoPagoRequestDTO dto) {
        log.info("[PAGO] PATCH /api/pagos/{}/estado - nuevoEstado={}", id, dto.getNuevoEstado());
        return ResponseEntity.ok(pagoService.cambiarEstado(id, dto.getNuevoEstado()));
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar un pago",
               description = "Marca el pago como COMPLETADO y registra la fecha de pago. Solo válido desde estado PENDIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago confirmado"),
            @ApiResponse(responseCode = "422", description = "El pago no está en estado PENDIENTE")
    })
    public ResponseEntity<PagoResponseDTO> confirmarPago(@PathVariable Long id) {
        log.info("[PAGO] PATCH /api/pagos/{}/confirmar", id);
        return ResponseEntity.ok(pagoService.confirmarPago(id));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un pago",
               description = "Cancela un pago en estado PENDIENTE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago cancelado"),
            @ApiResponse(responseCode = "422", description = "El pago no está en estado PENDIENTE")
    })
    public ResponseEntity<PagoResponseDTO> cancelarPago(@PathVariable Long id) {
        log.info("[PAGO] PATCH /api/pagos/{}/cancelar", id);
        return ResponseEntity.ok(pagoService.cancelarPago(id));
    }

    @PatchMapping("/{id}/fallido")
    @Operation(summary = "Marcar pago como fallido",
               description = "Registra el fallo de un pago (ej: tarjeta rechazada). Solo desde PENDIENTE.")
    @ApiResponse(responseCode = "200", description = "Pago marcado como fallido")
    public ResponseEntity<PagoResponseDTO> marcarFallido(@PathVariable Long id) {
        log.info("[PAGO] PATCH /api/pagos/{}/fallido", id);
        return ResponseEntity.ok(pagoService.marcarComoFallido(id));
    }

    @PatchMapping("/{id}/reembolsar")
    @Operation(summary = "Reembolsar un pago",
               description = "Procesa el reembolso de un pago COMPLETADO.")
    @ApiResponse(responseCode = "200", description = "Pago reembolsado")
    public ResponseEntity<PagoResponseDTO> reembolsar(@PathVariable Long id) {
        log.info("[PAGO] PATCH /api/pagos/{}/reembolsar", id);
        return ResponseEntity.ok(pagoService.reembolsarPago(id));
    }

    // ── GET: Reporte financiero ──────────────────────────────────────────────

    @GetMapping("/reporte/total-completados")
    @Operation(summary = "Total de pagos completados en un rango de fechas",
               description = "Retorna la suma de montos de todos los pagos COMPLETADOS entre las fechas indicadas.")
    @ApiResponse(responseCode = "200", description = "Total calculado correctamente")
    public ResponseEntity<BigDecimal> totalCompletados(
            @Parameter(description = "Fecha inicio (ISO)", example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @Parameter(description = "Fecha fin (ISO)", example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        log.info("[PAGO] GET /api/pagos/reporte/total-completados - desde={} hasta={}", desde, hasta);
        return ResponseEntity.ok(pagoService.calcularTotalCompletados(desde, hasta));
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pago",
               description = "Elimina permanentemente un pago. Solo permitido si está en estado CANCELADO o FALLIDO.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pago eliminado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "422", description = "No se puede eliminar un pago en este estado")
    })
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        log.info("[PAGO] DELETE /api/pagos/{}", id);
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }
}
