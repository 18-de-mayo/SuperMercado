package duoc.cl.despacho.controller;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.service.DespachoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/despachos")
@RequiredArgsConstructor
@Tag(name = "Microservicio Despacho", description = "Endpoints interactivos para el control, tracking e interoperabilidad de envíos físicos")
public class DespachoController {

    private final DespachoService service;

    // ── POST: CREAR DESPACHO ─────────────────────────────────────────
    @Operation(summary = "Registrar un nuevo despacho", description = "Valida síncronamente mediante Feign Client que el Pedido y el proveedor existan antes de persistir el envío en estado PENDIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Despacho registrado en forma exitosa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DespachoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Petición inválida o referencias lógicas inexistentes en los microservicios remotos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DespachoDTO> guardar(@Valid @RequestBody DespachoRequest request) {
        log.info("Petición HTTP POST: Registrando nuevo despacho para el pedido ID: {} con Proveedor ID: {}",
                request.getPedidoId(), request.getProveedorId());

        DespachoDTO nuevoDespacho = service.guardar(request);

        log.info("Despacho registrado exitosamente con ID asignado: {}", nuevoDespacho.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDespacho);
    }

    // ── GET: LISTAR TODOS ────────────────────────────────────────────
    @Operation(summary = "Listar todos los despachos", description = "Recupera el listado histórico de envíos, inyectando dinámicamente el nombre comercial del proveedor de transporte.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial consultado con éxito"),
            @ApiResponse(responseCode = "204", description = "No se encontraron registros de despachos", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DespachoDTO>> listar() {
        log.info("Petición HTTP GET: Solicitando el listado histórico de despachos");
        List<DespachoDTO> lista = service.listar();
        if (lista.isEmpty()) {
            log.warn("La base de datos no contiene registros de despachos en este momento.");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // ── GET: BUSCAR POR ID ───────────────────────────────────────────
    @Operation(summary = "Obtener despacho por ID", description = "Recupera un despacho específico mediante su ID numérico autoincremental.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despacho encontrado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DespachoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado en los registros", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DespachoDTO> buscarPorId(
            @Parameter(description = "ID único del despacho a consultar", example = "1")
            @PathVariable Long id) {
        log.info("Petición HTTP GET: Consultando despacho por ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ── PATCH: ACTUALIZAR ESTADO (Flujo crítico de negocio) ──────────
    @Operation(summary = "Actualizar estado del despacho", description = "Modifica el estado actual del envío siguiendo la máquina de estados obligatoria: PENDIENTE -> EN_RUTA -> ENTREGADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DespachoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado", content = @Content),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida o ilegal según reglas de negocio", content = @Content)
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoDTO> actualizarEstado(
            @Parameter(description = "ID del despacho a modificar", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado (EN_RUTA o ENTREGADO)", example = "EN_RUTA")
            @RequestParam String nuevoEstado) {
        log.info("Petición HTTP PATCH: Solicitando actualizar estado del despacho ID: {} hacia el nuevo estado: {}", id, nuevoEstado);
        return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado.toUpperCase()));
    }
}