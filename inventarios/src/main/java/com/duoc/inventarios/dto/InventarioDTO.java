package com.duoc.inventarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// DTO de salida — datos que se retornan al consultar un inventario
@Data
@Schema(description = "DTO que representa el inventario de un producto")
public class InventarioDTO {
    @Schema(description = "ID del registro de inventario")
    private Integer id;
    @Schema(description = "ID del producto")
    private Long productoId;
    @Schema(description = "Stock disponible actual")
    private Integer stockDisponible;
    @Schema(description = "Stock mínimo permitido")
    private Integer stockMinimo;
    @Schema(description = "Fecha de última actualización")
    private String fechaActualizacion;
}