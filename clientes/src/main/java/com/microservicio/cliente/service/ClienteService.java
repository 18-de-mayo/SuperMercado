package com.microservicio.cliente.service;//¿se usa una suerte de interface ya que así?: se planea un contrato. asi las funciones siempre existirán

import com.microservicio.cliente.dto.ClienteRequestDTO;//los datos que se reciben de la api 
import com.microservicio.cliente.dto.ClienteResponseDTO;//el cliente que envia la api, el cliente que se muestra en la api, el cliente que se responde a la api, el cliente que se devuelve a la api, el cliente que se retorna a la api, el cliente que se manda a la api, el cliente que se entrega a la api, el cliente que se proporciona a la api, el cliente que se ofrece a la api, el cliente que se suministra a la api, el cliente que se facilita a la api, el cliente que se concede a la api, el cliente que se otorga a la api, el cliente que se asigna a la api, el cliente que se distribuye a la api, el cliente que se comparte con la api, el cliente que se intercambia con la api, el cliente que se transfiere a la api.
import com.microservicio.cliente.model.Cliente.EstadoCliente;//¿

import java.util.List;

/**
 * Contrato de la capa de servicio para la gestión de clientes.
 */
public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO dto);//ClienteRequestDTO lo recibe la api si es que es aceptado

    ClienteResponseDTO obtenerClientePorId(Long id);//solo pide un numero para funcionar

    ClienteResponseDTO obtenerClientePorEmail(String email);//solo pide texto

    ClienteResponseDTO obtenerClientePorRut(String rut);//texto

    List<ClienteResponseDTO> listarClientes();//no pide nada, ni siquiera una paginacion

    List<ClienteResponseDTO> listarClientesPorEstado(EstadoCliente estado);//6 //filtro

    List<ClienteResponseDTO> buscarPorNombre(String texto);//7 similitud de texto

    ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO dto);// id para asociar y una muestra de los datos a actualizar

    ClienteResponseDTO cambiarEstado(Long id, EstadoCliente nuevoEstado);//sigo sin entender para que queremos a los clientes con estado: será para un historico, supongo. 

    void eliminarCliente(Long id);//10

    boolean clienteEstaActivo(Long id);//pregunta
}
