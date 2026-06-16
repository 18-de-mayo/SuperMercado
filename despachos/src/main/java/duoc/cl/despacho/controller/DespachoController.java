package duoc.cl.despacho.controller;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.service.DespachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST — expone los endpoints del dominio Despacho
@RestController
@RequestMapping("/api/v1/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService service;

    // POST /api/v1/despachos — crea un nuevo despacho validando pedido y proveedor
    @PostMapping
    public ResponseEntity<DespachoDTO> guardar(@Valid @RequestBody DespachoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(request));
    }

    // GET /api/v1/despachos — lista todos los despachos
    @GetMapping
    public ResponseEntity<List<DespachoDTO>> listar() {
        List<DespachoDTO> lista = service.listar();
        return lista.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(lista);
    }

    // GET /api/v1/despachos/{id} — busca un despacho por ID
    @GetMapping("/{id}")
    public ResponseEntity<DespachoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // PUT /api/v1/despachos/{id}/estado — avanza el estado del despacho
    // Body: { "estado": "EN_RUTA" } o { "estado": "ENTREGADO" }
    @PutMapping("/{id}/estado")
    public ResponseEntity<DespachoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(service.actualizarEstado(id, estado.toUpperCase()));
    }
}