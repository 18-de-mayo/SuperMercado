package duoc.cl.categoria.controller;

import duoc.cl.categoria.dto.CategoriaDTO;
import duoc.cl.categoria.dto.CategoriaRequest;
import duoc.cl.categoria.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías V1", description = "Endpoints para la gestión síncrona y estructurada de categorías (JSON Plano)")
public class CategoriaController {

    private final CategoriaService service;

    @Operation(summary = "Crear una nueva categoría", description = "Registra una categoría en el sistema mapeando el DTO de entrada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estructura DTO inválida o parámetros faltantes")
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaRequest request) {
        log.info("Petición entrante [POST] para crear categoría con datos: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(request));
    }

    @Operation(summary = "Listar todas las categorías", description = "Retorna un arreglo plano con todas las categorías almacenadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No existen registros en la base de datos")
    })
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listar() {
        log.info("Petición entrante [GET] para listar la totalidad de las categorías.");
        List<CategoriaDTO> lista = service.listar();
        return lista.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(lista);
    }

    @Operation(summary = "Buscar una categoría por ID", description = "Obtiene los detalles de una categoría específica mediante su identificador numérico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "El identificador no corresponde a ningún registro")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscar(@PathVariable Long id) {
        log.info("Petición entrante [GET] para localizar la categoría con ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Actualizar una categoría existente", description = "Reemplaza los atributos de una categoría basándose en su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría modificada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada erróneos"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        log.info("Petición entrante [PUT] para modificar la categoría ID: {} con los datos: {}", id, request);
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar una categoría", description = "Remueve físicamente el registro de la categoría del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito"),
            @ApiResponse(responseCode = "404", description = "ID no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.warn("Petición entrante [DELETE] - Intento de remoción física para la categoría ID: {}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}