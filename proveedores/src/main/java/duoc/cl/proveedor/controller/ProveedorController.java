package duoc.cl.proveedor.controller;

import duoc.cl.proveedor.dto.ProveedorDTO;
import duoc.cl.proveedor.dto.ProveedorRequest;
import duoc.cl.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedor")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService service;

    // GET ALL (Ahora usando ResponseEntity)
    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listar() {
        List<ProveedorDTO> proveedores = service.listar();
        if (proveedores.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(proveedores);
    }

    // GET BY ID (Ahora usando ResponseEntity)
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    // POST (Se agrega @Valid y ResponseEntity)
    @PostMapping
    public ResponseEntity<ProveedorDTO> guardar(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(request));
    }

    // PUT (Nuevo: Para actualizar un proveedor existente)
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    // DELETE (Nuevo: Para eliminar un proveedor)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}