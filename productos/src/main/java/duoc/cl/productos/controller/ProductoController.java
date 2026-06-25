package duoc.cl.productos.controller;

import duoc.cl.productos.dto.*;
import duoc.cl.productos.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
@Tag(name = "Productos", description = "Operaciones relacionadas con la gestión del catálogo de productos e integraciones distribuidas")
public class ProductoController {

    private final ProductoService productoService;


    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // 1. Guardar producto con validaciones locales y remotas
    @Operation(summary = "Guardar producto", description = "Crea un nuevo producto en el catálogo local. Valida que el nombre no esté duplicado y verifica la existencia del proveedor en el microservicio remoto mediante OpenFeign.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o id del proveedor nulo", content = @Content),
            @ApiResponse(responseCode = "404", description = "El proveedor asociado no existe en el sistema remoto", content = @Content),
            @ApiResponse(responseCode = "409", description = "El nombre del producto ya existe", content = @Content),

    })
    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(@Valid @RequestBody ProductoRequest request) {
        log.info("Recibida petición HTTP POST para guardar producto: {}", request.getNombre());
        ProductoDTO nuevoProducto = productoService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // 2. Listar productos con filtros opcionales
    @Operation(summary = "Listar o buscar productos", description = "Retorna productos con filtros opcionales por nombre, stock o paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(
            @Parameter(description = "Nombre o coincidencia parcial", example = "coca-cola")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar solo productos con stock", example = "true")
            @RequestParam(required = false) Boolean conStock,
            @Parameter(description = "Número de página (0-indexado)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int pagina,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(required = false, defaultValue = "100") int tamano) {
        if (nombre != null && !nombre.isBlank()) {
            log.info("GET /api/v1/productos?nombre={} - Buscando productos", nombre);
            return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
        }
        if (Boolean.TRUE.equals(conStock)) {
            log.info("GET /api/v1/productos?conStock=true - Listando con stock disponible");
            return ResponseEntity.ok(productoService.listarConStock());
        }
        if (pagina > 0 || tamano != 100) {
            log.info("GET /api/v1/productos?pagina={}&tamano={} - Listado paginado", pagina, tamano);
            Pageable pageable = PageRequest.of(pagina, tamano);
            return ResponseEntity.ok(productoService.listarPaginado(pageable).getContent());
        }
        log.info("GET /api/v1/productos - Listando todos los productos");
        return ResponseEntity.ok(productoService.listar());
    }

    // 3. Buscar un producto por su id primario
    @Operation(summary = "Buscar producto por ID", description = "Retorna un producto específico según su identificador numérico único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscar(
            @Parameter(description = "ID del producto a buscar", required = true, example = "1") @PathVariable Long id) {
        log.info("GET /api/v1/productos/{} - Buscando producto por ID", id);
        return ResponseEntity.ok(productoService.buscar(id));
    }

    // 7. Actualizar producto existente con re-validación de nombre y proveedor
    @Operation(summary = "Actualizar un producto", description = "Modifica los atributos de un producto existente. Realiza re-validación de nombre duplicado y existencia del proveedor remoto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto o proveedor remoto no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "El nuevo nombre ingresado ya está asignado a otro producto", content = @Content),
            @ApiResponse(responseCode = "502", description = "El microservicio remoto de proveedores no responde", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        log.info("Recibida petición HTTP PUT para actualizar producto ID: {}", id);
        ProductoDTO productoActualizado = productoService.actualizar(id, request);
        return ResponseEntity.ok(productoActualizado);
    }

    // 8. Eliminación por ID
    @Operation(summary = "Eliminar un producto", description = "Remueve físicamente un registro de producto de la base de datos MySQL usando su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true, example = "1") @PathVariable Long id) {
        log.info("Recibida petición HTTP DELETE para eliminar producto ID: {}", id);
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // 9. Integración distribuida por OpenFeign con microservicio de categorías
    @Operation(summary = "Listar productos por Categoría Remota", description = "Establece comunicación distribuida con el microservicio de categorías mediante OpenFeign para verificar la validez del ID de categoría antes de listar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos de la categoría remota listados con éxito",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductoDTO.class)))),
            @ApiResponse(responseCode = "204", description = "La categoría existe pero no tiene productos asociados", content = @Content),
            @ApiResponse(responseCode = "404", description = "La categoría especificada no existe en el microservicio remoto", content = @Content),
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> listarPorCategoriaRemota(@PathVariable Long categoriaId) {
        log.info("Recibida petición HTTP GET para filtrar por categoría remota ID: {}", categoriaId);
        return ResponseEntity.ok(productoService.listarPorCategoriaRemota(categoriaId));
    }
}