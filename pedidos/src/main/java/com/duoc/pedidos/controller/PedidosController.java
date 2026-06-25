package com.duoc.pedidos.controller;


import com.duoc.pedidos.dto.PedidoDTO;
import com.duoc.pedidos.dto.PedidoRequest;

import com.duoc.pedidos.service.PedidosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "API para la gestión de pedidos")
public class PedidosController {
    @Autowired
    private PedidosService pedidosService;

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Registra un nuevo pedido en el sistema validando la existencia del cliente a través del microservicio remoto de clientes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoRequest request) {
        log.info("POST /api/v1/pedidos - Creando pedido para cliente ID: {}", request.getIdCliente());
        return new ResponseEntity<>(pedidosService.crearPedido(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos", description = "Retorna todos los pedidos almacenados en el sistema. Si no existen pedidos registrados, retorna 204 No Content.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay pedidos registrados")
    })
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos() {
        log.info("GET /api/v1/pedidos - Listando todos los pedidos");
        List<PedidoDTO> pedidos = pedidosService.obtenerPedidos();
        if (pedidos.isEmpty()) {
            log.info("No se encontraron pedidos registrados");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Se encontraron {} pedidos", pedidos.size());
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Busca y retorna un pedido específico por su identificador único. Si no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable @Parameter(description = "ID del pedido", example = "1") Long id) {
        log.info("GET /api/v1/pedidos/{} - Buscando pedido por ID", id);
        return new ResponseEntity<>(pedidosService.buscarPorId(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pedido existente", description = "Actualiza los datos de un pedido existente, incluyendo cliente, dirección y lista de productos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<PedidoDTO> actualizarPedido(@PathVariable @Parameter(description = "ID del pedido", example = "1") Long id, @Valid @RequestBody PedidoRequest request) {
        log.info("PUT /api/v1/pedidos/{} - Actualizando pedido", id);
        return new ResponseEntity<>(pedidosService.actualizarPedido(id,request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido", description = "Elimina un pedido del sistema por su ID. Si no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Void> eliminarPedido(@PathVariable @Parameter(description = "ID del pedido", example = "1") Long id) {
        log.info("DELETE /api/v1/pedidos/{} - Eliminando pedido", id);
        pedidosService.eliminarPedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
