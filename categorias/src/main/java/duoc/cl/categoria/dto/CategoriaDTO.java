package duoc.cl.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de transferencia de datos (DTO) para la salida de información de una Categoría")
public class CategoriaDTO {

    @Schema(description = "Identificador único de la categoría en la base de datos", example = "1")
    private Long id;

    @Schema(description = "Nombre único de la categoría de productos", example = "Bebestibles")
    private String nombre;

    @Schema(description = "Descripción detallada del alcance de la categoría", example = "Productos líquidos, bebidas, jugos y aguas")
    private String descripcion;
}