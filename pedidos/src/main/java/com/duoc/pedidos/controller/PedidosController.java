package com.duoc.pedidos.controller;


import com.duoc.pedidos.dto.PedidoDTO;
import com.duoc.pedidos.dto.PedidosRequest;
import com.duoc.pedidos.repository.PedidosRepository;
import com.duoc.pedidos.service.PedidosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "API para la gestión de pedidos")
public class PedidosController {
    @Autowired
    private PedidosService pedidosService;

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidosRequest request) {
        return new ResponseEntity<>(pedidosService.crearPedido(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay pedidos registrados")
    })
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos() {
        List<PedidoDTO> pedidos = pedidosService.obtenerPedidos();
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Integer id) {
        return new ResponseEntity<>(pedidosService.buscarPorId(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pedido existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<PedidoDTO> actualizarPedido(@PathVariable Integer id,@Valid @RequestBody PedidosRequest request) {
        return new ResponseEntity<>(pedidosService.actualizarPedido(id,request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Void> eliminarPedido(@PathVariable Integer id) {
        pedidosService.eliminarPedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
