package com.microservicio.pago.exception;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador centralizado de excepciones para el microservicio de pagos.
 * Transforma excepciones en respuestas HTTP estructuradas y consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── Errores de dominio ──────────────────────────────────────────────────

    @ExceptionHandler(PagoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePagoNotFound(PagoNotFoundException ex) {
        log.warn("[PAGO] Pago no encontrado: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PagoYaExisteException.class)
    public ResponseEntity<Map<String, Object>> handlePagoYaExiste(PagoYaExisteException ex) {
        log.warn("[PAGO] Conflicto: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EstadoPagoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleEstadoInvalido(EstadoPagoInvalidoException ex) {
        log.warn("[PAGO] Transición de estado inválida: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // ── Errores de validación Bean Validation ───────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.put(fe.getField(), fe.getDefaultMessage());
        }
        log.warn("[PAGO] Error de validación en request: {}", errores);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Errores de validación");
        body.put("detalles", errores);
        return ResponseEntity.badRequest().body(body);
    }

    // ── Errores de comunicación con otros microservicios (Feign) ───────────

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Map<String, Object>> handleFeignNotFound(FeignException.NotFound ex) {
        String mensaje = "Recurso remoto no encontrado: verifique que el pedido y el cliente existen.";
        log.error("[PAGO] Feign 404 al comunicarse con otro microservicio: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignError(FeignException ex) {
        String mensaje = "Error de comunicación con microservicio remoto. Intente más tarde.";
        log.error("[PAGO] Error Feign (status {}): {}", ex.status(), ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, mensaje);
    }

    // ── Formato de petición inválido ─────────────────────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("[PAGO] Formato de peticion invalido: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Formato de petición inválido. Verifique los tipos de datos enviados.");
    }

    // ── Error genérico ──────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("[PAGO] Error inesperado: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.");
    }

    // ── Utilidad ────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
