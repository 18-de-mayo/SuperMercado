package com.duoc.inventarios.controller;

import com.duoc.inventarios.dto.InventarioDTO;
import com.duoc.inventarios.dto.InventariosRequest;
import com.duoc.inventarios.service.InventariosService;
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
public class inventariosController {

    @Autowired
    private InventariosService inventariosService;

    // POST /api/v1/inventarios — crea un nuevo registro de inventario
    @PostMapping
    public ResponseEntity<InventarioDTO> guardarInventario(@Valid @RequestBody InventariosRequest request) {
        log.info("El request para crear un inventario fue: " + request);
        return new ResponseEntity<>(inventariosService.crearInventario(request), HttpStatus.CREATED);
    }

    // GET /api/v1/inventarios — lista todos los inventarios
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> obtenerInventarios() {
        List<InventarioDTO> inventarios = inventariosService.obtenerInventarios();
        if (inventarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(inventarios, HttpStatus.OK);
    }

    // GET /api/v1/inventarios/{id} — busca un inventario por ID
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> buscarInventarioPorId(@PathVariable Integer id) {
        return new ResponseEntity<>(inventariosService.buscarInventarioPorId(id), HttpStatus.OK);
    }

    // PUT /api/v1/inventarios/{id} — actualiza un inventario existente
    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizarInventario(
            @PathVariable Integer id,
            @Valid @RequestBody InventariosRequest request) {
        return new ResponseEntity<>(inventariosService.actualizarInventario(id, request), HttpStatus.OK);
    }

    // DELETE /api/v1/inventarios/{id} — elimina un inventario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Integer id) {
        inventariosService.eliminarInventario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}