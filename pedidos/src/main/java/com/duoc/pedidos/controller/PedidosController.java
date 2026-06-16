package com.duoc.pedidos.controller;


import com.duoc.pedidos.dto.PedidoDTO;
import com.duoc.pedidos.dto.PedidosRequest;
import com.duoc.pedidos.repository.PedidosRepository;
import com.duoc.pedidos.service.PedidosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/pedidos")
public class PedidosController {
    @Autowired
    private PedidosService pedidosService;

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidosRequest request) {
        return new ResponseEntity<>(pedidosService.crearPedido(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerPedidos() {
        List<PedidoDTO> pedidos = pedidosService.obtenerPedidos();
        if (pedidos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Integer id) {
        return new ResponseEntity<>(pedidosService.buscarPorId(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizarPedido(@PathVariable Integer id,@Valid @RequestBody PedidosRequest request) {
        return new ResponseEntity<>(pedidosService.actualizarPedido(id,request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Integer id) {
        pedidosService.eliminarPedido(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
