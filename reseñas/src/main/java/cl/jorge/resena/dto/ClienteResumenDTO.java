package cl.jorge.resena.dto;

import lombok.Data;

/**
 * DTO para recibir datos remotos del microservicio de Clientes.
 * IE 2.4.1: Mapea la respuesta del Feign Client del servicio de clientes.
 */
@Data
public class ClienteResumenDTO {
    private Long id;
    private String nombre;
    private String rut;
    private Double nivel;
}
