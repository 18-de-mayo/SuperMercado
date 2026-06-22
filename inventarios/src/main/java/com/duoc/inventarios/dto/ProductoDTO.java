package com.duoc.inventarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// DTO que mapea la respuesta del MS producto al validar existencia
@Data
@Schema(description = "DTO con datos básicos del producto")
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private String nombreProveedor;
}