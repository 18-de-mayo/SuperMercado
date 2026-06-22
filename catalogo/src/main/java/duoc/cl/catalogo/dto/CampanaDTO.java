package duoc.cl.catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "DTO que representa una campaña promocional")
public class CampanaDTO {
    @Schema(description = "ID de la campaña")
    private Long id;
    @Schema(description = "Nombre de la campaña")
    private String nombreCampana;
    @Schema(description = "Lista de productos en la campaña")
    private List<CatalogoItemDTO> items;
}