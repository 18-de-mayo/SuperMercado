package com.microservicio.pago.dto;

import com.microservicio.pago.model.Pago.EstadoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para solicitudes de cambio de estado de un pago.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de cambio de estado de un pago")
public class EstadoPagoRequestDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    @Schema(description = "Nuevo estado deseado para el pago",
            allowableValues = {"COMPLETADO", "FALLIDO", "CANCELADO", "REEMBOLSADO"},
            example = "COMPLETADO",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private EstadoPago nuevoEstado;
}
