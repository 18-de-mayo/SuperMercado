package cl.jorge.resena.controller;

import cl.jorge.resena.dto.*;
import cl.jorge.resena.model.EstadoResena;
import cl.jorge.resena.service.ResenaService;
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

    @PostMapping
    public ResponseEntity<ResenaDTO> crearResena(@Valid @RequestBody ResenaRequest request) {
        log.info("Petición REST [POST /resenas] recibida para clienteId={}, productoId={}",
                request.getClienteId(), request.getProductoId());
        ResenaDTO nuevaResena = resenaService.crearResena(request);
        log.info("Reseña creada exitosamente con ID: {}", nuevaResena.getId());
        return new ResponseEntity<>(nuevaResena, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ResenaDTO>> obtenerTodas() {
        log.info("Petición REST [GET /resenas] recibida para LISTAR todas las reseñas");
        List<ResenaDTO> resenas = resenaService.obtenerTodas();
        return ResponseEntity.ok(resenas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Petición REST [GET /resenas/{}] recibida", id);
        ResenaDTO resena = resenaService.obtenerPorId(id);
        return ResponseEntity.ok(resena);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ResenaDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        log.info("Petición REST [GET /resenas/cliente/{}] recibida", clienteId);
        List<ResenaDTO> resenas = resenaService.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(resenas);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        log.info("Petición REST [GET /resenas/producto/{}] recibida", productoId);
        List<ResenaDTO> resenas = resenaService.obtenerPorProducto(productoId);
        return ResponseEntity.ok(resenas);
    }

    @GetMapping("/producto/{productoId}/aprobadas")
    public ResponseEntity<List<ResenaDTO>> obtenerAprobadasPorProducto(@PathVariable Long productoId) {
        log.info("Petición REST [GET /resenas/producto/{}/aprobadas] recibida", productoId);
        List<ResenaDTO> resenas = resenaService.obtenerResenaAprobadasPorProducto(productoId);
        return ResponseEntity.ok(resenas);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ResenaDTO>> obtenerPorEstado(@PathVariable EstadoResena estado) {
        log.info("Petición REST [GET /resenas/estado/{}] recibida", estado);
        List<ResenaDTO> resenas = resenaService.obtenerPorEstado(estado);
        return ResponseEntity.ok(resenas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> actualizarResena(
            @PathVariable Long id,
            @Valid @RequestBody ResenaRequest request) {
        log.info("Petición REST [PUT /resenas/{}] recibida para ACTUALIZAR reseña", id);
        ResenaDTO resenaActualizada = resenaService.actualizarResena(id, request);
        return ResponseEntity.ok(resenaActualizada);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ResenaDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.info("Petición REST [PATCH /resenas/{}/estado] recibida → nuevo estado: {}", id, request.getNuevoEstado());
        ResenaDTO resenaActualizada = resenaService.actualizarEstado(id, request);
        return ResponseEntity.ok(resenaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarResena(@PathVariable Long id) {
        log.info("Petición REST [DELETE /resenas/{}] recibida para ELIMINAR reseña", id);
        resenaService.eliminarResena(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{resenaId}/respuestas")
    public ResponseEntity<RespuestaResenaDTO> agregarRespuesta(
            @PathVariable Long resenaId,
            @Valid @RequestBody RespuestaResenaRequest request) {
        log.info("Petición REST [POST /resenas/{}/respuestas] recibida de autor: {}", resenaId, request.getAutor());
        RespuestaResenaDTO respuesta = resenaService.agregarRespuesta(resenaId, request);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping("/{resenaId}/respuestas")
    public ResponseEntity<List<RespuestaResenaDTO>> obtenerRespuestas(@PathVariable Long resenaId) {
        log.info("Petición REST [GET /resenas/{}/respuestas] recibida", resenaId);
        List<RespuestaResenaDTO> respuestas = resenaService.obtenerRespuestasPorResena(resenaId);
        return ResponseEntity.ok(respuestas);
    }

    @GetMapping("/producto/{productoId}/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenProducto(@PathVariable Long productoId) {
        log.info("Petición REST [GET /resenas/producto/{}/resumen] distribuida iniciada", productoId);
        Map<String, Object> resumen = resenaService.obtenerResumenProducto(productoId);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/cliente/{clienteId}/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenCliente(@PathVariable Long clienteId) {
        log.info("Petición REST [GET /resenas/cliente/{}/resumen] distribuida iniciada", clienteId);
        Map<String, Object> resumen = resenaService.obtenerResumenCliente(clienteId);
        return ResponseEntity.ok(resumen);
    }
}