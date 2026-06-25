package cl.jorge.resena.exception;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Respuesta de error estándar")
public class ErrorResponse {
    @Schema(description = "Marca de tiempo del error")
    private LocalDateTime timestamp;
    @Schema(description = "Código de error HTTP", example = "400")
    private int status;
    @Schema(description = "Descripción del error", example = "Solicitud inválida")
    private String error;
    @Schema(description = "Lista de mensajes de error")
    private List<String> messages;
}
