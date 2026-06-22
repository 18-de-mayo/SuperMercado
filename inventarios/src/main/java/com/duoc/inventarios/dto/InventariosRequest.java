package com.duoc.inventarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

// DTO de entrada — valida los datos al crear o actualizar un inventario
@Data
@Schema(description = "Solicitud para crear o actualizar un registro de inventario")
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

    @NotNull(message = "La fecha de actualización es obligatoria")
    private LocalDateTime fechaActualizacion;
}