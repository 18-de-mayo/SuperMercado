package com.duoc.pedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO con datos básicos del producto")
public class ProductoDTO {
    private Long id;
    private String nombre;
}
