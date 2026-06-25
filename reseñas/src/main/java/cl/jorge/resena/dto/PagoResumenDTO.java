package cl.jorge.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO con resumen de pago para reseñas (obtenido vía Feign desde MS pagos)")
public class PagoResumenDTO {
    @Schema(description = "ID del pago", example = "1")
    private Long id;

    @Schema(description = "ID del pedido asociado", example = "10")
    private Long pedidoId;

    @Schema(description = "Número de recibo", example = "REC-2025-000001")
    private String numeroRecibo;

    @Schema(description = "Monto del pago", example = "15990.50")
    private BigDecimal monto;

    @Schema(description = "Método de pago", example = "TARJETA_DEBITO")
    private String metodoPago;

    @Schema(description = "Estado del pago", example = "COMPLETADO")
    private String estado;
}
