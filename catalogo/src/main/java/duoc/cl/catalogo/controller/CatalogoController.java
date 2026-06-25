package duoc.cl.catalogo.controller;

import duoc.cl.catalogo.dto.CampanaDTO;
import duoc.cl.catalogo.dto.CampanaRequest;
import duoc.cl.catalogo.dto.CatalogoItemDTO;
import duoc.cl.catalogo.dto.CatalogoRequest;
import duoc.cl.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/catalogos")
@Tag(name = "Catálogos", description = "API para la gestión de catálogos y campañas promocionales")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas las campañas", description = "Retorna todas las campañas promocionales registradas en el sistema con sus productos asociados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de campañas"),
        @ApiResponse(responseCode = "204", description = "No hay campañas registradas")
    })
    public ResponseEntity<List<CampanaDTO>> listarCampanas() {
        log.info("GET /api/v1/catalogos - Listando todas las campañas");
        var lista = service.listarCampanas();
        if (lista.isEmpty()) {
            log.info("No se encontraron campañas registradas");
            return ResponseEntity.noContent().build();
        }
        log.info("Se encontraron {} campañas", lista.size());
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva campaña", description = "Crea una nueva campaña promocional con el nombre especificado en el cuerpo de la solicitud.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Campaña creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<CampanaDTO> crearCampana(@Valid @RequestBody CampanaRequest request) {
        log.info("POST /api/v1/catalogos - Creando campaña con nombre: {}", request.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCampana(request.getNombre()));
    }

    @PostMapping("/{campanaId}/productos")
    @Operation(summary = "Agregar producto a una campaña", description = "Agrega un producto existente del catálogo maestro a una campaña promocional, con precio de catálogo y precio de oferta.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<CampanaDTO> agregarProducto(
            @PathVariable @Parameter(description = "ID de la campaña", example = "1") Long campanaId,
            @Valid @RequestBody CatalogoRequest request) {
        log.info("POST /api/v1/catalogos/{}/productos - Agregando producto ID: {}", campanaId, request.getProductoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarProductoACampana(
                campanaId,
                request.getProductoId(),
                request.getPrecioCatalogo(),
                request.getPrecioOferta()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una campaña por ID", description = "Retorna una campaña específica con todos sus productos asociados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaña encontrada"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public ResponseEntity<CampanaDTO> obtenerCampana(@PathVariable @Parameter(description = "ID de la campaña", example = "1") Long id) {
        log.info("GET /api/v1/catalogos/{} - Obteniendo campaña", id);
        return ResponseEntity.ok(service.obtenerCampana(id));
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "Obtener item individual del catálogo", description = "Retorna un producto individual del catálogo con información detallada incluyendo disponibilidad de stock desde el microservicio de inventarios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<CatalogoItemDTO> obtenerProductoIndividualDelCatalogo(@PathVariable @Parameter(description = "ID del producto en el catálogo", example = "1") Long id) {
        log.info("GET /api/v1/catalogos/items/{} - Obteniendo item individual", id);
        var item = service.obtenerItemIndividualPorId(id);
        log.info("Item con ID {} encontrado exitosamente", id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nombre de una campaña", description = "Actualiza el nombre de una campaña promocional existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaña actualizada"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public ResponseEntity<CampanaDTO> actualizarCampana(
            @PathVariable @Parameter(description = "ID de la campaña", example = "1") Long id,
            @Valid @RequestBody CampanaRequest request) {
        log.info("PUT /api/v1/catalogos/{} - Actualizando campaña a nombre: {}", id, request.getNombre());
        return ResponseEntity.ok(service.actualizarCampana(id, request.getNombre()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una campaña", description = "Elimina una campaña promocional del sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Campaña eliminada"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public ResponseEntity<Void> eliminarCampana(@PathVariable @Parameter(description = "ID de la campaña", example = "1") Long id) {
        log.info("DELETE /api/v1/catalogos/{} - Eliminando campaña", id);
        service.eliminarCampana(id);
        log.info("Campaña ID {} eliminada exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}