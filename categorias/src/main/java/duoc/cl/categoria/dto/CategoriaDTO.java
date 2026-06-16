package duoc.cl.categoria.dto;

import lombok.Data;

// DTO de salida — datos que se retornan al consultar una categoría
@Data
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}