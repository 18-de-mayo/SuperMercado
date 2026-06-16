package com.microservicio.pago.dto;

import com.microservicio.pago.model.Pago.EstadoPago;
import com.microservicio.pago.model.Pago.MetodoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida con los datos completos de un pago.
 * Enviado en las respuestas HTTP de los endpoints del controlador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta completa de un pago registrado")
public class PagoResponseDTO {

    @Schema(description = "ID único del pago", example = "1")
    private Long id;

    @Schema(description = "ID del pedido asociado", example = "10")
    private Long pedidoId;

    @Schema(description = "ID del cliente que realizó el pago", example = "3")
    private Long clienteId;

    @Schema(description = "Número de recibo generado automáticamente", example = "REC-2025-000001")
    private String numeroRecibo;

    @Schema(description = "Monto total pagado", example = "15990.50")
    private BigDecimal monto;

    @Schema(description = "Método de pago utilizado", example = "TARJETA_DEBITO")
    private MetodoPago metodoPago;

    @Schema(description = "Estado actual del pago", example = "COMPLETADO")
    private EstadoPago estado;

    @Schema(description = "Notas adicionales del pago", example = "Voucher #ABC123")
    private String notas;

    @Schema(description = "Fecha y hora de creación del pago")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha y hora de última actualización")
    private LocalDateTime fechaActualizacion;

    @Schema(description = "Fecha y hora en que el pago fue confirmado como COMPLETADO")
    private LocalDateTime fechaPago;
}
