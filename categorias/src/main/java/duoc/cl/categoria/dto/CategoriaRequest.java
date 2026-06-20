package duoc.cl.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de solicitud (Request DTO) para la creación o actualización de una Categoría con reglas de validación")
public class CategoriaRequest {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre único que identificará a la categoría de productos",
            example = "Carnes y Aves",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 2,
            maxLength = 100)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Schema(description = "Descripción complementaria de los tipos de productos incluidos",
            example = "Cortes de vacuno, pollo, cerdo y embutidos frescos",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            maxLength = 255)
    private String descripcion;
}