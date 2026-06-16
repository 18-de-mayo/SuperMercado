package cl.jorge.resena.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura estándar de respuesta de error para todos los endpoints.
 * IE 2.3.1: Respuestas controladas y uniformes ante errores del dominio.
 */
@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<String> messages;
}
