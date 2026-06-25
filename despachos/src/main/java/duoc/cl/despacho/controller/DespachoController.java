package duoc.cl.despacho.controller;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.model.EstadoDespacho;
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
        log.info("POST /api/v1/despachos - Creando despacho para pedido ID: {}", request.getPedidoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(request));
    }

    // ── GET: LISTAR TODOS ────────────────────────────────────────────
    @Operation(summary = "Listar todos los despachos", description = "Recupera el listado histórico de envíos, inyectando dinámicamente el nombre comercial del proveedor de transporte.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial consultado con éxito"),
            @ApiResponse(responseCode = "204", description = "No se encontraron registros de despachos", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DespachoDTO>> listar() {
        log.info("GET /api/v1/despachos - Listando todos los despachos");
        List<DespachoDTO> lista = service.listar();
        if (lista.isEmpty()) {
            log.info("No se encontraron despachos registrados");
            return ResponseEntity.noContent().build();
        }
        log.info("Se encontraron {} despachos", lista.size());
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
        log.info("GET /api/v1/despachos/{} - Buscando despacho por ID", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // ── PATCH: CAMBIAR ESTADO ──────────────────────────────────────────

    @Operation(summary = "Actualizar estado del despacho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado inválida")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoDespacho estado) {
        log.info("PATCH /api/v1/despachos/{}/estado - Nuevo estado: {}", id, estado);
        return ResponseEntity.ok(service.actualizarEstado(id, estado));
    }

    // ── PUT: ACTUALIZAR DESPACHO COMPLETO ────────────────────────────
    @Operation(summary = "Actualizar despacho", description = "Actualiza los datos completos de un despacho existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despacho actualizado con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DespachoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DespachoDTO> actualizar(
            @Parameter(description = "ID del despacho a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody DespachoRequest request) {
        log.info("PUT /api/v1/despachos/{} - Actualizando despacho", id);
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // ── DELETE: ELIMINAR DESPACHO ────────────────────────────────────
    @Operation(summary = "Eliminar despacho", description = "Elimina un despacho del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Despacho eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del despacho a eliminar", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/v1/despachos/{} - Eliminando despacho", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}