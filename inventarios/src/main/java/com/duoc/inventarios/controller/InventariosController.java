package com.duoc.inventarios.controller;

import com.duoc.inventarios.dto.InventarioDTO;
import com.duoc.inventarios.dto.InventariosRequest;
import com.duoc.inventarios.service.InventariosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST — expone los endpoints del dominio Inventario
@Slf4j
@RestController
@RequestMapping("/api/v1/inventarios")
@Tag(name = "Inventarios", description = "API para la gestión de inventarios del sistema")
public class InventariosController {

    @Autowired
    private InventariosService inventariosService;


    @Operation(summary = "Registrar inventario", description = "Crea un nuevo registro de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = InventarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
    })
    @PostMapping
    public ResponseEntity<InventarioDTO> guardarInventario(@Valid @RequestBody InventariosRequest request) {
        log.info("El request para crear un inventario fue: " + request);
        return new ResponseEntity<>(inventariosService.crearInventario(request), HttpStatus.CREATED);
    }


    @Operation(summary = "Listar inventarios", description = "Se listan todos los inventarios existentes")
    @ApiResponse(responseCode = "200", description = "Lista ls inventarios")
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> obtenerInventarios() {
        List<InventarioDTO> inventarios = inventariosService.obtenerInventarios();
        if (inventarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(inventarios, HttpStatus.OK);
    }


    @Operation(summary = "Buscar inventario por ID")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "inventario encontrado"),
                    @ApiResponse(responseCode = "404", description = "invantario no encontrado")})
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> buscarInventarioPorId(@PathVariable Integer id) {
        return new ResponseEntity<>(inventariosService.buscarInventarioPorId(id), HttpStatus.OK);
    }

    // PUT /api/v1/inventarios/{id} — actualiza un inventario existente
    @Operation(summary = "Actualizar registro de un inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro de inventario actualizado"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")})
    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizarInventario(
            @PathVariable Integer id,
            @Valid @RequestBody InventariosRequest request) {
        return new ResponseEntity<>(inventariosService.actualizarInventario(id, request), HttpStatus.OK);
    }

    // DELETE /api/v1/inventarios/{id} — elimina un inventario
    @Operation(summary = "Eliminar inventario existente")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Inventario elimando"),
                    @ApiResponse(responseCode = "404", description = "Inventario no encontrado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Integer id) {
        inventariosService.eliminarInventario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}