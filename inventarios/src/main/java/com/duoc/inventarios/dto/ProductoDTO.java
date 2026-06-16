package com.duoc.inventarios.dto;

import lombok.Data;

// DTO que mapea la respuesta del MS producto al validar existencia
@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private String nombreProveedor;
}