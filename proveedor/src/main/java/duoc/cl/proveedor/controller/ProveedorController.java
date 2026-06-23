package duoc.cl.proveedor.controller;

import duoc.cl.proveedor.dto.*;
import duoc.cl.proveedor.service.ProveedorService;

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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@Slf4j
@Tag(name = "Proveedores", description = "Operaciones relacionadas con la gestión del registro de proveedores en el sistema")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // 1. Guardar proveedor con validaciones de negocio locales
    @Operation(summary = "Guardar proveedor", description = "Crea un nuevo proveedor en el registro local. Valida que el RUT sea único y que los campos requeridos cumplan con las restricciones del sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProveedorDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o faltantes", content = @Content),
            @ApiResponse(responseCode = "409", description = "El RUT o correo electrónico ingresado ya existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProveedorDTO> guardar(@Valid @RequestBody ProveedorRequest request) {
        log.info("Recibida petición HTTP POST para guardar proveedor: {}", request.getNombre());
        ProveedorDTO nuevoProveedor = proveedorService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    // 2. Listar todos los proveedores registrados sin paginación
    @Operation(summary = "Listar proveedores", description = "Retorna todos los proveedores almacenados en la base de datos local.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProveedorDTO.class))))
    })
    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listar() {
        log.info("Recibida petición HTTP GET para listar todos los proveedores");
        return ResponseEntity.ok(proveedorService.listar());
    }

    // 3. Buscar un proveedor por su identificador primario
    @Operation(summary = "Buscar proveedor por ID", description = "Retorna un proveedor específico según su identificador numérico único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProveedorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> buscar(
            @Parameter(description = "ID del proveedor a buscar", required = true, example = "1") @PathVariable Long id) {
        log.info("Recibida petición HTTP GET para buscar proveedor por ID: {}", id);
        return ResponseEntity.ok(proveedorService.buscar(id));
    }
}