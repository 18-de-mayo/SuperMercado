package duoc.cl.catalogo.controller;

import duoc.cl.catalogo.dto.CampanaDTO;
import duoc.cl.catalogo.dto.CatalogoRequest; // Usamos tu Request actual
import duoc.cl.catalogo.service.CatalogoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/catalogos")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @GetMapping
    public String holaMundo() {
        return "hola Mundo!";
    }

    // 1. Crear la campaña vacía: POST http://localhost:8083/api/v1/catalogos?nombre=Catálogo Invierno
    @PostMapping
    public ResponseEntity<CampanaDTO> crearCampana(@RequestParam String nombre) {
        return ResponseEntity.ok(service.crearCampana(nombre));
    }

    // 2. Agregar productos a la campaña
    @PostMapping("/{campanaId}")
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
    public ResponseEntity<CampanaDTO> obtenerCampana(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerCampana(id));
    }

    // 4. Endpoint para OpenFeign: Obtener un ítem individual del catálogo por su ID de producto
    @GetMapping("/items/{id}")
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
}