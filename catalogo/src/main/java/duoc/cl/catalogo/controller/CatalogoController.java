package duoc.cl.catalogo.controller;

import duoc.cl.catalogo.dto.CampanaDTO;
import duoc.cl.catalogo.dto.CatalogoRequest; // Usamos tu Request actual
import duoc.cl.catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "Listar todas las campañas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de campañas"),
        @ApiResponse(responseCode = "204", description = "No hay campañas registradas")
    })
    public ResponseEntity<List<CampanaDTO>> listarCampanas() {
        var lista = service.listarCampanas();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    // 1. Crear la campaña vacía: POST http://localhost:8083/api/v1/catalogos?nombre=Catálogo Invierno
    @PostMapping
    @Operation(summary = "Crear una nueva campaña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Campaña creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<CampanaDTO> crearCampana(@RequestParam String nombre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCampana(nombre));
    }

    // 2. Agregar productos a la campaña
    @PostMapping("/{campanaId}")
    @Operation(summary = "Agregar producto a una campaña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<CampanaDTO> agregarProducto(
            @PathVariable Long campanaId,
            @RequestBody CatalogoRequest request) {

        return ResponseEntity.ok(service.agregarProductoACampana(
                campanaId,
                request.getProductoId(),
                request.getPrecioCatalogo(),
                request.getPrecioOferta()
        ));
    }

    // 3. Obtener la campaña completa por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener una campaña por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaña encontrada"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public ResponseEntity<CampanaDTO> obtenerCampana(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerCampana(id));
    }

    // 4. Endpoint para OpenFeign: Obtener un ítem individual del catálogo por su ID de producto
    @GetMapping("/items/{id}")
    @Operation(summary = "Obtener item individual del catálogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item encontrado"),
        @ApiResponse(responseCode = "404", description = "Item no encontrado")
    })
    public ResponseEntity<?> obtenerProductoIndividualDelCatalogo(@PathVariable Long id) {
        log.info("Feign llamó exitosamente a [GET /catalogos/items/{}]", id);
        try {
            // Llama a la lógica de negocio para recuperar el producto único
            var item = service.obtenerItemIndividualPorId(id);
            return ResponseEntity.ok(item);
        } catch (RuntimeException ex) {
            log.error("Error al buscar producto individual con ID {}: {}", id, ex.getMessage());
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nombre de una campaña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaña actualizada"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public ResponseEntity<CampanaDTO> actualizarCampana(
            @PathVariable Long id,
            @RequestParam String nombre) {
        return ResponseEntity.ok(service.actualizarCampana(id, nombre));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una campaña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Campaña eliminada"),
        @ApiResponse(responseCode = "404", description = "Campaña no encontrada")
    })
    public ResponseEntity<Void> eliminarCampana(@PathVariable Long id) {
        service.eliminarCampana(id);
        return ResponseEntity.noContent().build();
    }
}