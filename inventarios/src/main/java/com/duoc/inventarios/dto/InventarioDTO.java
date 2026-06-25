package com.duoc.inventarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

// DTO de salida — datos que se retornan al consultar un inventario
@Data
@Schema(description = "DTO que representa el inventario de un producto")
public class InventarioDTO {
    @Schema(description = "ID del registro de inventario", example = "1")
    private Long id;
    @Schema(description = "ID del producto", example = "100")
    private Long productoId;
    @Schema(description = "Stock disponible actual", example = "100")
    private Integer stockDisponible;
    @Schema(description = "Stock mínimo permitido", example = "10")
    private Integer stockMinimo;
    @Schema(description = "Fecha de última actualización", example = "2024-03-20T10:30:00")
    private LocalDateTime fechaActualizacion;
}