package cl.jorge.resena.controller;

import cl.jorge.resena.dto.*;
import cl.jorge.resena.model.EstadoResena;
import cl.jorge.resena.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Map;
@Tag(name = "reseñas", description = "Operaciones relacionadas con las reseñas")
@Slf4j
@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @Operation(summary = "Crear una nueva reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<ResenaDTO> crearResena(@Valid @RequestBody ResenaRequest request) {
        log.info("Petición REST [POST /resenas] recibida para clienteId={}, productoId={}",
                request.getClienteId(), request.getProductoId());
        ResenaDTO nuevaResena = resenaService.crearResena(request);
        log.info("Reseña creada exitosamente con ID: {}", nuevaResena.getId());
        return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las reseñas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reseñas obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay reseñas disponibles")
    })
    @GetMapping
    public ResponseEntity<List<ResenaDTO>> obtenerTodas() {
        log.info("Petición REST [GET /resenas] recibida para LISTAR todas las reseñas");
        List<ResenaDTO> resenas = resenaService.obtenerTodas();
        return ResponseEntity.ok(resenas);
    }

    @Operation(summary = "Obtener reseña por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST [GET /resenas/{}] recibida", id);
        ResenaDTO resena = resenaService.obtenerPorId(id);
        return ResponseEntity.ok(resena);
    }

    @Operation(summary = "Obtener reseñas por ID de cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseñas del cliente obtenidas"),
        @ApiResponse(responseCode = "204", description = "El cliente no tiene reseñas")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ResenaDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        log.info("Petición REST [GET /resenas/cliente/{}] recibida", clienteId);
        List<ResenaDTO> resenas = resenaService.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(resenas);
    }

    @Operation(summary = "Obtener reseñas por ID de producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseñas del producto obtenidas"),
        @ApiResponse(responseCode = "204", description = "El producto no tiene reseñas")
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        log.info("Petición REST [GET /resenas/producto/{}] recibida", productoId);
        List<ResenaDTO> resenas = resenaService.obtenerPorProducto(productoId);
        return ResponseEntity.ok(resenas);
    }

    @Operation(summary = "Obtener reseñas aprobadas de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseñas aprobadas obtenidas"),
        @ApiResponse(responseCode = "204", description = "No hay reseñas aprobadas")
    })
    @GetMapping("/producto/{productoId}/aprobadas")
    public ResponseEntity<List<ResenaDTO>> obtenerAprobadasPorProducto(@PathVariable Long productoId) {
        log.info("Petición REST [GET /resenas/producto/{}/aprobadas] recibida", productoId);
        List<ResenaDTO> resenas = resenaService.obtenerResenaAprobadasPorProducto(productoId);
        return ResponseEntity.ok(resenas);
    }

    @Operation(summary = "Obtener reseñas por estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseñas filtradas por estado obtenidas"),
        @ApiResponse(responseCode = "204", description = "No hay reseñas en ese estado")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ResenaDTO>> obtenerPorEstado(@PathVariable EstadoResena estado) {
        log.info("Petición REST [GET /resenas/estado/{}] recibida", estado);
        List<ResenaDTO> resenas = resenaService.obtenerPorEstado(estado);
        return ResponseEntity.ok(resenas);
    }

    @Operation(summary = "Actualizar una reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> actualizarResena(
            @PathVariable Long id,
            @Valid @RequestBody ResenaRequest request) {
        log.info("Petición REST [PUT /resenas/{}] recibida para ACTUALIZAR reseña", id);
        ResenaDTO resenaActualizada = resenaService.actualizarResena(id, request);
        return ResponseEntity.ok(resenaActualizada);
    }

    @Operation(summary = "Actualizar estado de una reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada"),
        @ApiResponse(responseCode = "422", description = "Entidad no procesable, estado inválido")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ResenaDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.info("Petición REST [PATCH /resenas/{}/estado] recibida → nuevo estado: {}", id, request.getNuevoEstado());
        ResenaDTO resenaActualizada = resenaService.actualizarEstado(id, request);
        return ResponseEntity.ok(resenaActualizada);
    }

    @Operation(summary = "Eliminar una reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reseña eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        log.info("Petición REST [DELETE /resenas/{}] recibida para ELIMINAR reseña", id);
        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Agregar respuesta a una reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Respuesta agregada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    @PostMapping("/{resenaId}/respuestas")
    public ResponseEntity<RespuestaResenaDTO> agregarRespuesta(
            @PathVariable Long resenaId,
            @Valid @RequestBody RespuestaResenaRequest request) {
        log.info("Petición REST [POST /resenas/{}/respuestas] recibida de autor: {}", resenaId, request.getAutor());
        RespuestaResenaDTO respuesta = resenaService.agregarRespuesta(resenaId, request);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener respuestas de una reseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuestas obtenidas exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay respuestas para esta reseña")
    })
    @GetMapping("/{resenaId}/respuestas")
    public ResponseEntity<List<RespuestaResenaDTO>> obtenerRespuestas(@PathVariable Long resenaId) {
        log.info("Petición REST [GET /resenas/{}/respuestas] recibida", resenaId);
        List<RespuestaResenaDTO> respuestas = resenaService.obtenerRespuestasPorResena(resenaId);
        return ResponseEntity.ok(respuestas);
    }

    @Operation(summary = "Obtener resumen de reseñas de un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/producto/{productoId}/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenProducto(@PathVariable Long productoId) {
        log.info("Petición REST [GET /resenas/producto/{}/resumen] distribuida iniciada", productoId);
        Map<String, Object> resumen = resenaService.obtenerResumenProducto(productoId);
        return ResponseEntity.ok(resumen);
    }

    @Operation(summary = "Obtener resumen de reseñas de un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/cliente/{clienteId}/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenCliente(@PathVariable Long clienteId) {
        log.info("Petición REST [GET /resenas/cliente/{}/resumen] distribuida iniciada", clienteId);
        Map<String, Object> resumen = resenaService.obtenerResumenCliente(clienteId);
        return ResponseEntity.ok(resumen);
    }
}