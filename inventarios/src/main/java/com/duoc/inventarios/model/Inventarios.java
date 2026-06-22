package com.duoc.inventarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad JPA — representa la tabla 'inventarios' en la base de datos
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "inventarios")
@Schema(description = "Entidad que representa el inventario en la base de datos")
public class Inventarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ID del producto en el MS producto — referencia lógica, no FK de BD
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    // Fecha manual — el cliente indica cuándo se actualizó el inventario
    @Column(name = "fecha_actualizacion", nullable = false)
    private String fechaActualizacion;
}