package com.microservicio.cliente.dto;

import com.microservicio.cliente.model.Cliente.EstadoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO utilizado para exponer datos del cliente en las respuestas REST.
 * No expone datos internos de la entidad que no deben ser públicos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO con los datos completos de un cliente")
public class ClienteResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String region;
    private LocalDate fechaNacimiento;
    private EstadoCliente estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    /** Nombre completo calculado como nombre + apellido */
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
}
