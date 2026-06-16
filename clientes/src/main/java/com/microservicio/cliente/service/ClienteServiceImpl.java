package com.microservicio.cliente.service;

import com.microservicio.cliente.dto.ClienteRequestDTO;
import com.microservicio.cliente.dto.ClienteResponseDTO;
import com.microservicio.cliente.exception.ClienteNotFoundException;
import com.microservicio.cliente.exception.ClienteYaExisteException;
import com.microservicio.cliente.exception.EstadoInvalidoException;
import com.microservicio.cliente.model.Cliente;
import com.microservicio.cliente.model.Cliente.EstadoCliente;
import com.microservicio.cliente.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la lógica de negocio para clientes.
 * Contiene las reglas de dominio: unicidad de RUT/email, validación de estado, etc.
 */
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;

    // ─────────────────────────── CREAR ───────────────────────────

    /**
     * Registra un nuevo cliente.
     * Regla de negocio: el email y el RUT deben ser únicos en el sistema.
     */
    @Override
    @Transactional
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        log.info("Intentando crear cliente con email: {}", dto.getEmail());

        // Regla de negocio: email único
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            log.warn("Intento de registro con email ya existente: {}", dto.getEmail());
            throw new ClienteYaExisteException("Ya existe un cliente con el email: " + dto.getEmail());
        }

        // Regla de negocio: RUT único
        if (clienteRepository.existsByRut(dto.getRut())) {
            log.warn("Intento de registro con RUT ya existente: {}", dto.getRut());
            throw new ClienteYaExisteException("Ya existe un cliente con el RUT: " + dto.getRut());
        }

        Cliente cliente = mapearDtoAEntidad(dto);
        Cliente guardado = clienteRepository.save(cliente);

        log.info("Cliente creado exitosamente con ID: {}", guardado.getId());
        return mapearEntidadADto(guardado);
    }

    // ─────────────────────────── LEER ────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorId(Long id) {
        log.debug("Buscando cliente con ID: {}", id);
        Cliente cliente = buscarClienteOLanzarExcepcion(id);
        return mapearEntidadADto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorEmail(String email) {
        log.debug("Buscando cliente con email: {}", email);
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("No se encontró cliente con email: " + email));
        return mapearEntidadADto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorRut(String rut) {
        log.debug("Buscando cliente con RUT: {}", rut);
        Cliente cliente = clienteRepository.findByRut(rut)
                .orElseThrow(() -> new ClienteNotFoundException("No se encontró cliente con RUT: " + rut));
        return mapearEntidadADto(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientes() {
        log.debug("Listando todos los clientes");
        return clienteRepository.findAll()
                .stream()
                .map(this::mapearEntidadADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientesPorEstado(EstadoCliente estado) {
        log.debug("Listando clientes con estado: {}", estado);
        return clienteRepository.findByEstado(estado)
                .stream()
                .map(this::mapearEntidadADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNombre(String texto) {
        log.debug("Buscando clientes por texto: {}", texto);
        return clienteRepository.buscarPorNombreOApellido(texto)
                .stream()
                .map(this::mapearEntidadADto)
                .collect(Collectors.toList());
    }

    // ─────────────────────────── ACTUALIZAR ───────────────────────

    /**
     * Actualiza los datos de un cliente existente.
     * Regla de negocio: no se puede cambiar el email/RUT a uno ya existente en otro cliente.
     */
    @Override
    @Transactional
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO dto) {
        log.info("Actualizando cliente con ID: {}", id);
        Cliente cliente = buscarClienteOLanzarExcepcion(id);

        // Regla: si cambia el email, verificar que no pertenezca a otro cliente
        if (!cliente.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                clienteRepository.existsByEmail(dto.getEmail())) {
            throw new ClienteYaExisteException("El email ya está en uso por otro cliente: " + dto.getEmail());
        }

        // Regla: si cambia el RUT, verificar que no pertenezca a otro cliente
        if (!cliente.getRut().equals(dto.getRut()) &&
                clienteRepository.existsByRut(dto.getRut())) {
            throw new ClienteYaExisteException("El RUT ya está en uso por otro cliente: " + dto.getRut());
        }

        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setRut(dto.getRut());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setCiudad(dto.getCiudad());
        cliente.setRegion(dto.getRegion());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());

        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Cliente ID {} actualizado correctamente", id);
        return mapearEntidadADto(actualizado);
    }

    /**
     * Cambia el estado de un cliente.
     * Regla de negocio: un cliente SUSPENDIDO no puede volver a ACTIVO directamente;
     * debe pasar por INACTIVO primero.
     */
    @Override
    @Transactional
    public ClienteResponseDTO cambiarEstado(Long id, EstadoCliente nuevoEstado) {
        log.info("Cambiando estado del cliente ID {} a {}", id, nuevoEstado);
        Cliente cliente = buscarClienteOLanzarExcepcion(id);

        EstadoCliente estadoActual = cliente.getEstado();

        // Regla de negocio: SUSPENDIDO → ACTIVO no está permitido directamente
        if (estadoActual == EstadoCliente.SUSPENDIDO && nuevoEstado == EstadoCliente.ACTIVO) {
            throw new EstadoInvalidoException(
                    "Un cliente suspendido no puede pasar directamente a ACTIVO. Debe pasar por INACTIVO primero.");
        }

        cliente.setEstado(nuevoEstado);
        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Estado del cliente ID {} cambiado de {} a {}", id, estadoActual, nuevoEstado);
        return mapearEntidadADto(actualizado);
    }

    // ─────────────────────────── ELIMINAR ───────────────────────

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        Cliente cliente = buscarClienteOLanzarExcepcion(id);
        clienteRepository.delete(cliente);
        log.info("Cliente ID {} eliminado exitosamente", id);
    }

    // ─────────────────────── CONSULTA AUXILIAR ──────────────────

    /**
     * Verifica si un cliente está activo.
     * Usado por otros microservicios (ej: pedidos) antes de procesar.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean clienteEstaActivo(Long id) {
        log.debug("Verificando si el cliente ID {} está activo", id);
        Cliente cliente = buscarClienteOLanzarExcepcion(id);
        boolean activo = cliente.getEstado() == EstadoCliente.ACTIVO;
        log.debug("Cliente ID {} está activo: {}", id, activo);
        return activo;
    }

    // ─────────────────────────── HELPERS ────────────────────────

    private Cliente buscarClienteOLanzarExcepcion(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cliente no encontrado con ID: {}", id);
                    return new ClienteNotFoundException("No se encontró cliente con ID: " + id);
                });
    }

    private Cliente mapearDtoAEntidad(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .rut(dto.getRut())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .region(dto.getRegion())
                .fechaNacimiento(dto.getFechaNacimiento())
                .estado(EstadoCliente.ACTIVO)
                .build();
    }

    private ClienteResponseDTO mapearEntidadADto(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .rut(cliente.getRut())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .direccion(cliente.getDireccion())
                .ciudad(cliente.getCiudad())
                .region(cliente.getRegion())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .estado(cliente.getEstado())
                .fechaRegistro(cliente.getFechaRegistro())
                .fechaActualizacion(cliente.getFechaActualizacion())
                .build();
    }
}
