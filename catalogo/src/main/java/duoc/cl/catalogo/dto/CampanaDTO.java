package duoc.cl.catalogo.dto;

import lombok.Data;
import java.util.List;

@Data
public class CampanaDTO {
    private Long id;
    private String nombreCampana;
    private List<CatalogoItemDTO> items;
}