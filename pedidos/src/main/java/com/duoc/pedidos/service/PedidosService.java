package com.duoc.pedidos.service;

import com.duoc.pedidos.Client.ClienteClient;
import com.duoc.pedidos.dto.*;
import com.duoc.pedidos.exception.ClientesNotFoundException;
import com.duoc.pedidos.exception.PedidosNotFoundException;
import com.duoc.pedidos.model.DetallePedidos;
import com.duoc.pedidos.model.Pedidos;
import com.duoc.pedidos.repository.PedidosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidosService {

    @Autowired
    private PedidosRepository pedidosRepository;

    @Autowired
    private ClienteClient clienteClient;

    public PedidoDTO crearPedido(PedidosRequest request) {
        validarCliente(request.getIdCliente());

        Pedidos pedidos = new Pedidos();
        pedidos.setIdCliente(request.getIdCliente());
        pedidos.setFechaPedido(request.getFechaPedido());
        pedidos.setEstadoPedido(request.getEstadoPedido());

        List<DetallePedidos> listaDetalles = new ArrayList<>();
        if (request.getDetalles() != null) {
            for (DetallePedidosRequest detReq : request.getDetalles()) {
                DetallePedidos detalle = new DetallePedidos();
                detalle.setIdProducto(detReq.getIdProducto());
                detalle.setCantidad(detReq.getCantidad());
                detalle.setPrecioUnitario(detReq.getPrecioUnitario());

                detalle.setPedido(pedidos);

                listaDetalles.add(detalle);
            }
        }

        pedidos.setDetalles(listaDetalles);

        Pedidos pedidoGuardado = pedidosRepository.save(pedidos);

        return convertirADTO(pedidoGuardado);
    }

    public List<PedidoDTO> obtenerPedidos() {
        return pedidosRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public PedidoDTO buscarPorId(Integer id) {
        Pedidos pedidos = pedidosRepository.findById(id).orElseThrow(() -> new PedidosNotFoundException(id));
        return convertirADTO(pedidos);
    }

    public PedidoDTO actualizarPedido(Integer id, PedidosRequest request) {
        validarCliente(request.getIdCliente());

        Pedidos pedidosExistente = pedidosRepository.findById(id).orElseThrow(() -> new PedidosNotFoundException(id));

        pedidosExistente.setIdCliente(request.getIdCliente());
        pedidosExistente.setFechaPedido(request.getFechaPedido());
        pedidosExistente.setEstadoPedido(request.getEstadoPedido());
        return convertirADTO(pedidosRepository.save(pedidosExistente));
    }

    public void eliminarPedido(Integer id) {
        pedidosRepository.findById(id).orElseThrow(() -> new PedidosNotFoundException(id));
        pedidosRepository.deleteById(id);
    }

    private void validarCliente(Integer idCliente) {
        List<ClienteDTO> clientes = clienteClient.obtenerClientes();
        boolean existe = clientes.stream().anyMatch(c -> c.getId().equals(idCliente));
        if (!existe) throw new ClientesNotFoundException(idCliente);
    }

    private PedidoDTO convertirADTO(Pedidos pedidos) {
        if (pedidos == null) return null;
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedidos.getId());
        dto.setIdCliente(pedidos.getIdCliente());
        dto.setEstadoPedido(pedidos.getEstadoPedido());
        dto.setFechaPedido(pedidos.getFechaPedido());

        if (pedidos.getDetalles() != null) {
            List<DetallePedidoDTO> detallesDTO = pedidos.getDetalles().stream()
                    .map(detalle -> {
                        DetallePedidoDTO detDto = new DetallePedidoDTO();
                        detDto.setId(detalle.getId());
                        detDto.setIdProducto(detalle.getIdProducto());
                        detDto.setCantidad(detalle.getCantidad());
                        detDto.setPrecioUnitario(detalle.getPrecioUnitario());
                        return detDto;
                    })
                    .collect(Collectors.toList());

            dto.setDetalles(detallesDTO);
        }
        return dto;
    }
}
