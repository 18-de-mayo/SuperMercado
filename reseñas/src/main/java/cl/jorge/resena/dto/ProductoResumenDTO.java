package cl.jorge.resena.dto;

import lombok.Data;

/**
 * DTO para recibir datos remotos del microservicio de Productos.
 * IE 2.4.1: Mapea la respuesta del Feign Client del servicio de productos.
 */
@Data
public class ProductoResumenDTO {
    private Long id;
    private String nombre;
    private Double precio;
}
