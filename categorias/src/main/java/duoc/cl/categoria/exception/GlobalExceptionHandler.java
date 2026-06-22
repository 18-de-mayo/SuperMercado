package duoc.cl.categoria.exception;

import duoc.cl.categoria.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // REQUERIMIENTO: Captura y documentación de la respuesta 404 (ID no localizado)
    @ExceptionHandler(CategoriaNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "La categoría solicitada no existe en la base de datos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleNotFound(CategoriaNotFoundException ex) {
        log.error("Recurso no localizado en la base de datos: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // REQUERIMIENTO: Captura y documentación de errores de negocio (Ej: Nombre de categoría duplicado)
    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(responseCode = "400", description = "Violación de reglas de negocio de la aplicación",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleBusinessRules(IllegalArgumentException ex) {
        log.warn("Conflicto de regla de negocio detectado: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // REQUERIMIENTO: Captura y documentación de errores de validación de los DTOs (@NotBlank, @Size)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Errores de validación en los campos del formulario de entrada",
            content = @Content(schema = @Schema(example = "{\"nombre\": \"El nombre de la categoría es obligatorio\", \"descripcion\": \"La descripción no puede superar los 255 caracteres\"}")))
    public ResponseEntity<Map<String, String>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        log.warn("Fallo en las validaciones perimetrales del DTO de entrada: {}", errores);
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}