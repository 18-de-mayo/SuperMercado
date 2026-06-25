package com.microservicio.cliente.controller;

import com.microservicio.cliente.dto.ClienteRequestDTO;
import com.microservicio.cliente.dto.ClienteResponseDTO;
import com.microservicio.cliente.model.Cliente.EstadoCliente;
import com.microservicio.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.Map;

/**
 * Controlador REST del microservicio de clientes.
 * Solo orquesta peticiones HTTP, toda la lógica reside en ClienteService.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para la gestión de clientes del sistema")
public class ClienteController {

    private final ClienteService clienteService;

    // ─────────────────────── POST ───────────────────────

    @Operation(
            summary = "Registrar nuevo cliente",
            description = "Crea un nuevo cliente en el sistema. El email y RUT deben ser únicos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
            @ApiResponse(responseCode = "409", description = "Email o RUT ya registrado")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crearCliente(
            @Valid @RequestBody ClienteRequestDTO dto) {
        log.info("POST /api/v1/clientes - Creando cliente: {}", dto.getEmail());
        ClienteResponseDTO creado = clienteService.crearCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // ─────────────────────── GET ────────────────────────

    @Operation(summary = "Listar o buscar clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes(
            @Parameter(description = "Texto a buscar en nombre o apellido", example = "Juan")
            @RequestParam(required = false) String texto) {
        if (texto != null && !texto.isBlank()) {
            log.info("GET /api/v1/clientes?texto={} - Buscando clientes", texto);
            return ResponseEntity.ok(clienteService.buscarPorNombre(texto));
        }
        log.info("GET /api/v1/clientes - Listando todos los clientes");
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(
            @Parameter(description = "ID del cliente", example = "1")
            @PathVariable Long id) {
        log.info("GET /api/v1/clientes/{} - Obteniendo cliente", id);
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }

    @Operation(summary = "Obtener cliente por email")
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorEmail(
            @Parameter(description = "Email del cliente", example = "juan@email.com")
            @PathVariable String email) {
        log.info("GET /api/v1/clientes/email/{} - Buscando cliente", email);
        return ResponseEntity.ok(clienteService.obtenerClientePorEmail(email));
    }

    @Operation(summary = "Obtener cliente por RUT")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorRut(
            @Parameter(description = "RUT del cliente", example = "12345678-9")
            @PathVariable String rut) {
        log.info("GET /api/v1/clientes/rut/{} - Buscando cliente", rut);
        return ResponseEntity.ok(clienteService.obtenerClientePorRut(rut));
    }

    @Operation(summary = "Listar clientes por estado",
               description = "Filtra clientes según su estado: ACTIVO, INACTIVO o SUSPENDIDO")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ClienteResponseDTO>> listarPorEstado(
            @PathVariable EstadoCliente estado) {
        log.info("GET /api/v1/clientes/estado/{} - Filtrando clientes", estado);
        return ResponseEntity.ok(clienteService.listarClientesPorEstado(estado));
    }

    /**
     * Endpoint interno: verificar si un cliente está activo.
     * Usado por el microservicio de pedidos antes de procesar una orden.
     */
    @Operation(
            summary = "Verificar si cliente está activo",
            description = "Endpoint de uso interno para que otros microservicios (ej: pedidos) " +
                          "verifiquen si un cliente puede operar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del cliente",
                    content = @Content(examples = @ExampleObject(value = "{\"activo\": true}"))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{id}/activo")
    public ResponseEntity<Map<String, Boolean>> verificarActivo(@PathVariable Long id) {
        log.info("GET /api/v1/clientes/{}/activo - Verificando estado", id);
        boolean activo = clienteService.clienteEstaActivo(id);
        return ResponseEntity.ok(Map.of("activo", activo));
    }

    // ─────────────────────── PUT ────────────────────────

    @Operation(summary = "Actualizar datos de un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email o RUT en uso por otro cliente")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto) {
        log.info("PUT /api/v1/clientes/{} - Actualizando cliente", id);
        return ResponseEntity.ok(clienteService.actualizarCliente(id, dto));
    }

    @Operation(
            summary = "Cambiar estado del cliente",
            description = "Cambia el estado de un cliente. Restricción: SUSPENDIDO no puede pasar directamente a ACTIVO."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado cambiado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "422", description = "Transición de estado no permitida")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ClienteResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "INACTIVO")
            @RequestParam EstadoCliente estado) {
        log.info("PATCH /api/v1/clientes/{}/estado - Cambiando a {}", id, estado);
        return ResponseEntity.ok(clienteService.cambiarEstado(id, estado));
    }

    // ─────────────────────── DELETE ─────────────────────

    @Operation(summary = "Eliminar un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        log.info("DELETE /api/v1/clientes/{} - Eliminando cliente", id);
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
