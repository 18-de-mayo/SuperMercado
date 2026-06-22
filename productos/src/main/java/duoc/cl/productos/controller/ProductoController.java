package duoc.cl.productos.controller;

import duoc.cl.productos.dto.*;
import duoc.cl.productos.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "API para la gestión de productos")
public class ProductoController {

    @Autowired
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(
            @Valid @RequestBody ProductoRequest request){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoService.guardar(request));
    }

    @Operation(summary = "Listar todos los productos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos registrados")
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(){

        List<ProductoDTO> productos = productoService.listar();

        if(productos.isEmpty()){

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(productos);
    }
    @Operation(summary = "Buscar producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscar(@PathVariable Long id){

        return ResponseEntity.ok(productoService.buscar(id));
    }

    @Operation(summary = "Buscar productos por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(
            @Parameter(name = "nombre", description = "Nombre o parte del nombre a buscar") @RequestParam String nombre){

        return ResponseEntity.ok(
                productoService.buscarPorNombre(nombre)
        );
    }
    @Operation(summary = "Listar productos con stock disponible")
    @GetMapping("/stock")
    public ResponseEntity<List<ProductoDTO>> listarConStock(){

        return ResponseEntity.ok(productoService.listarConStock());
    }


    @Operation(summary = "Listar productos con paginación")
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoDTO>> listarPaginado(
            Pageable pageable){

        return ResponseEntity.ok(productoService.listarPaginado(pageable));
    }

    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un producto")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}