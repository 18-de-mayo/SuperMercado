package com.microservicio.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO utilizado para la creación y actualización de clientes.
 * Separa la capa de validación de la entidad JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Solicitud para crear o actualizar un cliente")
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @NotBlank(message = "El RUT no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}-[\\dkK]", message = "El RUT debe tener el formato 12345678-9")
    @Schema(description = "RUT del cliente", example = "12345678-9")
    private String rut;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    @Schema(description = "Correo electrónico", example = "juan@email.com")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "\\+?[0-9]{9,15}", message = "El teléfono debe contener entre 9 y 15 dígitos")
    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String telefono;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 255)
    @Schema(description = "Dirección del cliente")
    private String direccion;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Schema(description = "Ciudad de residencia")
    private String ciudad;

    @NotBlank(message = "La región no puede estar vacía")
    @Schema(description = "Región de residencia")
    private String region;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Schema(description = "Fecha de nacimiento")
    private LocalDate fechaNacimiento;
}
