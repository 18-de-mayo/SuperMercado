package duoc.cl.catalogo.service;

import duoc.cl.catalogo.dto.CampanaDTO;
import duoc.cl.catalogo.dto.CatalogoItemDTO;
import duoc.cl.catalogo.dto.ProductoDTO;
import duoc.cl.catalogo.feign.ProductoFeignClient;
import duoc.cl.catalogo.model.CatalogoCampana;
import duoc.cl.catalogo.model.CatalogoItem;
import duoc.cl.catalogo.repository.CatalogoCampanaRepository;
import duoc.cl.catalogo.repository.CatalogoItemRepository; // Importamos el repositorio de ítems
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogoService {

    private final CatalogoCampanaRepository campanaRepository;
    private final CatalogoItemRepository itemRepository; // Agregado para persistencia segura
    private final ProductoFeignClient productoFeignClient;

    public CatalogoService(CatalogoCampanaRepository campanaRepository,
                           CatalogoItemRepository itemRepository,
                           ProductoFeignClient productoFeignClient) {
        this.campanaRepository = campanaRepository;
        this.itemRepository = itemRepository;
        this.productoFeignClient = productoFeignClient;
    }

    // ====================================================================
    // CORREGIDO: Creación limpia para evitar Error 500
    // ====================================================================
    public CampanaDTO crearCampana(String nombreCampana) {
        CatalogoCampana campana = new CatalogoCampana();
        campana.setNombreCampana(nombreCampana);

        // Dejamos que JPA maneje la inicialización por defecto configurada en la entidad
        CatalogoCampana campanaGuardada = campanaRepository.save(campana);

        if (campanaGuardada.getItems() == null) {
            campanaGuardada.setItems(new ArrayList<>());
        }

        return mapToCampanaDTO(campanaGuardada);
    }

    // ====================================================================
    // OPTIMIZADO: Guardado transaccional y directo usando itemRepository
    // ====================================================================
    public CampanaDTO agregarProductoACampana(Long campanaId, Long productoId, BigDecimal precioCat, BigDecimal precioOf) {
        CatalogoCampana campana = campanaRepository.findById(campanaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaña no encontrada"));

        // Validar vía Feign que el producto exista
        try {
            productoFeignClient.buscarProducto(productoId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no existe en el catálogo maestro.");
        }

        // Creamos el ítem apuntando a su campaña padre
        CatalogoItem item = new CatalogoItem();
        item.setProductoId(productoId);
        item.setPrecioCatalogo(precioCat);
        item.setPrecioOferta(precioOf);
        item.setCampana(campana);

        // Guardamos el ítem individualmente para asegurar la consistencia en la base de datos
        itemRepository.save(item);

        // Retornamos la campaña actualizada refrescando los datos desde el repositorio
        CatalogoCampana campanaActualizada = campanaRepository.findById(campanaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaña no encontrada"));

        return mapToCampanaDTO(campanaActualizada);
    }

    // 3. Obtener una campaña por ID (con todos sus productos mapeados)
    public CampanaDTO obtenerCampana(Long id) {
        CatalogoCampana campana = campanaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaña no encontrada"));
        return mapToCampanaDTO(campana);
    }

    //
    public CatalogoItemDTO obtenerItemIndividualPorId(Long itemId) {
        // Busca por el ID del item directamente
        CatalogoItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El ítem de catálogo no existe con ID: " + itemId));

        return mapToItemDTO(item);
    }

    // Mapeadores útiles
    private CampanaDTO mapToCampanaDTO(CatalogoCampana campana) {
        CampanaDTO dto = new CampanaDTO();
        dto.setId(campana.getId());
        dto.setNombreCampana(campana.getNombreCampana());

        if (campana.getItems() != null) {
            dto.setItems(campana.getItems().stream().map(this::mapToItemDTO).collect(Collectors.toList()));
        } else {
            dto.setItems(new ArrayList<>());
        }

        return dto;
    }

    private CatalogoItemDTO mapToItemDTO(CatalogoItem item) {
        CatalogoItemDTO dto = new CatalogoItemDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProductoId());
        dto.setPrecioCatalogo(item.getPrecioCatalogo());
        dto.setPrecioOferta(item.getPrecioOferta());

        try {
            ProductoDTO prod = productoFeignClient.buscarProducto(item.getProductoId());
            dto.setNombreProducto(prod.getNombre());
            dto.setDescripcion(prod.getDescripcion());
            dto.setNombreProveedor(prod.getNombreProveedor());
            dto.setEstadoStock(prod.getCantidad() >= 1 ? "Disponible" : "Sin stock");
        } catch (Exception e) {
            dto.setNombreProducto("No disponible");
            dto.setDescripcion("El producto fue eliminado.");
            dto.setNombreProveedor("N/A");
            dto.setEstadoStock("No disponible");
        }
        return dto;
    }
}