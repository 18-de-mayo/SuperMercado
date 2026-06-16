package com.microservicio.pago.dto;

import com.microservicio.pago.model.Pago.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO de entrada para crear o actualizar un pago.
 * Separa la capa de presentación de la entidad JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos necesarios para registrar un pago")
public class PagoRequestDTO {

    @NotNull(message = "El ID del pedido es obligatorio")
    @Positive(message = "El ID del pedido debe ser un número positivo")
    @Schema(description = "ID del pedido a pagar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pedidoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto debe tener máximo 10 enteros y 2 decimales")
    @Schema(description = "Monto total del pago", example = "15990.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    @Schema(description = "Método de pago utilizado",
            allowableValues = {"EFECTIVO", "TARJETA_DEBITO", "TARJETA_CREDITO", "TRANSFERENCIA_BANCARIA", "VALE_VISTA"},
            example = "TARJETA_DEBITO",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private MetodoPago metodoPago;

    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
    @Schema(description = "Notas opcionales del pago (voucher, referencia, etc.)", example = "Voucher #ABC123")
    private String notas;
}
