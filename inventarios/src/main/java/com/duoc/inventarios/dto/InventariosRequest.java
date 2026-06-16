package com.duoc.inventarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// DTO de entrada — valida los datos al crear o actualizar un inventario
@Data
public class InventariosRequest {

    @NotNull(message = "El id del producto no puede ser nulo")
    @Positive(message = "El id del producto debe ser mayor a cero")
    private Long productoId;        // corregido a Long para coincidir con MS producto

    @NotNull(message = "El stock disponible no puede ser nulo")
    @Positive(message = "El stock disponible debe ser mayor a cero")
    private Integer stockDisponible;

    @NotNull(message = "El stock mínimo no puede ser nulo")
    @Positive(message = "El stock mínimo debe ser mayor a cero")
    private Integer stockMinimo;

    @NotBlank(message = "La fecha de actualización no puede estar vacía")
    private String fechaActualizacion;
}