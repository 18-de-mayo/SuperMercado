package duoc.cl.proveedor.controller;

import duoc.cl.proveedor.dto.ProveedorDTO;
import duoc.cl.proveedor.dto.ProveedorRequest;
import duoc.cl.proveedor.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedor")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "API para la gestión de proveedores")
public class ProveedorController {

    private final ProveedorService service;

    @Operation(summary = "Listar todos los proveedores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay proveedores registrados")
    })
    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listar() {
        List<ProveedorDTO> proveedores = service.listar();
        if (proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(proveedores);
    }

    @Operation(summary = "Buscar proveedor por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Crear un nuevo proveedor")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<ProveedorDTO> guardar(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(request));
    }

    @Operation(summary = "Actualizar un proveedor existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor actualizado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @Operation(summary = "Eliminar un proveedor")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proveedor eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}