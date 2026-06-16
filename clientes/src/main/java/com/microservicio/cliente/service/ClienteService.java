package com.microservicio.cliente.service;

import com.microservicio.cliente.dto.ClienteRequestDTO;
import com.microservicio.cliente.dto.ClienteResponseDTO;
import com.microservicio.cliente.model.Cliente.EstadoCliente;

import java.util.List;

/**
 * Contrato de la capa de servicio para la gestión de clientes.
 */
public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO dto);

    ClienteResponseDTO obtenerClientePorId(Long id);

    ClienteResponseDTO obtenerClientePorEmail(String email);

    ClienteResponseDTO obtenerClientePorRut(String rut);

    List<ClienteResponseDTO> listarClientes();

    List<ClienteResponseDTO> listarClientesPorEstado(EstadoCliente estado);

    List<ClienteResponseDTO> buscarPorNombre(String texto);

    ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO dto);

    ClienteResponseDTO cambiarEstado(Long id, EstadoCliente nuevoEstado);

    void eliminarCliente(Long id);

    boolean clienteEstaActivo(Long id);
}
