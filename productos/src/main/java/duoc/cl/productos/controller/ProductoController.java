package duoc.cl.productos.controller;

import duoc.cl.productos.dto.*;
import duoc.cl.productos.service.ProductoService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // guardar producto
    @PostMapping
    public ResponseEntity<ProductoDTO> guardar(
            @Valid @RequestBody ProductoRequest request){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoService.guardar(request));
    }

    // listar productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(){

        List<ProductoDTO> productos = productoService.listar();

        if(productos.isEmpty()){

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(productos);
    }
    //bsucar por id
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscar(@PathVariable Long id){

        return ResponseEntity.ok(productoService.buscar(id));
    }

    // buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(
            @RequestParam String nombre){

        return ResponseEntity.ok(
                productoService.buscarPorNombre(nombre)
        );
    }
    // productos con stock
    @GetMapping("/stock")
    public ResponseEntity<List<ProductoDTO>> listarConStock(){

        return ResponseEntity.ok(productoService.listarConStock());
    }


    // paginacion
    @GetMapping("/paginado")
    public ResponseEntity<Page<ProductoDTO>> listarPaginado(
            Pageable pageable){

        return ResponseEntity.ok(productoService.listarPaginado(pageable));
    }

    // PUT actualizar un producto)
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    // DELETE eliminar un producto)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}