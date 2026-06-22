package duoc.cl.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Fallback", description = "Endpoint de fallback unificado para circuit breakers")
@RestController
public class FallbackController {

    @Operation(summary = "Respuesta de fallback para circuit breakers")
    @ApiResponse(responseCode = "503", description = "Servicio no disponible temporalmente")
    @GetMapping("/fallback/{servicio}")
    public ResponseEntity<Map<String, Object>> fallback(@PathVariable String servicio) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", 503,
                        "error", "Service Unavailable",
                        "message", "El servicio " + servicio + " no está disponible en este momento. Intente nuevamente más tarde.",
                        "service", servicio
                ));
    }
}
