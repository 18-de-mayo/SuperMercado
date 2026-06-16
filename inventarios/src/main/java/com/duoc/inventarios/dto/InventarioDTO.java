package com.duoc.inventarios.dto;

import lombok.Data;

// DTO de salida — datos que se retornan al consultar un inventario
@Data
public class InventarioDTO {
    private Integer id;
    private Long productoId;
    private Integer stockDisponible;
    private Integer stockMinimo;
    private String fechaActualizacion;
}