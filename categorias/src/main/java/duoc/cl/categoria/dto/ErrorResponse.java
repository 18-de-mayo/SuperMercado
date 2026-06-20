package duoc.cl.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Al usar Builder aquí, los constructores explícitos de arriba son obligatorios para Jackson
@Schema(description = "Estructura estándar para la notificación de errores en la API")
public class ErrorResponse {

    @Schema(description = "Código de estado HTTP del error", example = "404")
    private int status;

    @Schema(description = "Mensaje descriptivo del error ocurrido", example = "No se encontró la categoría con ID: 99")
    private String message;

    @Schema(description = "Fecha y hora exacta en la que se generó la excepción")
    private LocalDateTime timestamp;
}