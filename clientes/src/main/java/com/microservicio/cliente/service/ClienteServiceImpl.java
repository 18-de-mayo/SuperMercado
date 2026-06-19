package com.microservicio.cliente.service;//clase servicio... implementacion, del package service

import com.microservicio.cliente.dto.ClienteRequestDTO;//los que se solicitan en la api
import com.microservicio.cliente.dto.ClienteResponseDTO;//los que se envian limados
import com.microservicio.cliente.exception.ClienteNotFoundException;//excepcion especifica para cliente no encontrado
import com.microservicio.cliente.exception.ClienteYaExisteException;//excepcion especifica para cliente ya existente
import com.microservicio.cliente.exception.EstadoInvalidoException;//¿qué es estado inavlido?: 
import com.microservicio.cliente.model.Cliente;//¿el modelado original?: no, no?
import com.microservicio.cliente.model.Cliente.EstadoCliente;// ¿esto es una subclase?: enumeracion dentro de la clase
import com.microservicio.cliente.repository.ClienteRepository;//repositorio porque con el nos comunicamos. 
import lombok.RequiredArgsConstructor;//lombok
import org.slf4j.Logger;//logger de slf4j es?:
import org.slf4j.LoggerFactory;//factoria de logs por qué, por qué usa la palabra factory?: 
import org.springframework.stereotype.Service;//estereotype ya dice mucho
import org.springframework.transaction.annotation.Transactional;// transactional

import java.util.List;// sintacticamente es un tipo?: sí
import java.util.stream.Collectors;//collectors para hacer lista de entidades una lista de DTOs

/**
 * Implementación de la lógica de negocio para clientes.
 * Contiene las reglas de dominio: unicidad de RUT/email, validación de estado, etc.
 */
@Service//marca esta clase coo un servicio, es decir, la capa de servicio, la capa intermedia entre el controlador y el repositorio
@RequiredArgsConstructor//para atributos finales, siendo final para inyeccion. @RequiredArgsConstructor actua sobre los atributos finales. 
public class ClienteServiceImpl implements ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);//privado stattico final Logger log = metodo con parametros, paramtros que significan

    private final ClienteRepository clienteRepository;

    // ─────────────────────────── CREAR ───────────────────────────

    /**
     * Registra un nuevo cliente.
     * Regla de negocio: el email y el RUT deben ser únicos en el sistema.
     */
    @Override// porque viene de una interface,
    @Transactional// cumple su objetivo a pesar de que no haya un uno es a muchos?: 
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {//¿request porque solicita el cliente que se ocupen estos datos o request porque nosotros solicitamos esos datos al cliente?
        log.info("Intentando crear cliente: {}", dto.getNombre());

        // Regla de negocio: email único
        if (clienteRepository.existsByEmail(dto.getEmail())) {//si es verdadero que existe un cliente con el email dado...
            log.warn("Intento de registro con email ya existente: {}", dto.getEmail());//se anota un warn para alimentar los logs
            throw new ClienteYaExisteException("Ya existe un cliente con el email: " + dto.getEmail());//tirar nuevo ClienteYaExisteExcepcion con argumento String
        }

        // Regla de negocio: RUT único
        if (clienteRepository.existsByRut(dto.getRut())) {//si coincide en la base de datos este rut?: 
            log.warn("Intento de registro con RUT ya existente: {}", dto.getRut());//se logea el intento
            throw new ClienteYaExisteException("Ya existe un cliente con el RUT: " + dto.getRut());//¿se corta la ejecucion de la funcion por la simple palabra throw?:
        }

        Cliente cliente = mapearDtoAEntidad(dto);//tipo Cliente, varible cliente, igual a la funcion mapearDtoAEntidad con el argumento traido de la api
        Cliente guardado = clienteRepository.save(cliente);//variable "guardado" es el "cliente" ya seteado con los atributos del modelado original pero sin el id. ¿qué devuelve un .save?: ¿envia "y" trae devuelta los datos del cliente?: 

        log.info("Cliente creado exitosamente con ID: {}", guardado.getId());//se informa la creacion y de paso se espoilea el id 
        return mapearEntidadADto(guardado);// en lo que se retorna un "ClienteRespondeDTO" se aprovecha de "a la variable 'guaradado'" filtrarla al molde "Response". o me equivoco?: 
    }

    // ─────────────────────────── LEER ────────────────────────────

    @Override//tipico a estas alturas
    @Transactional(readOnly = true)//se aclara el transactional ya que: para optimizar el rendimiento
    public ClienteResponseDTO obtenerClientePorId(Long id) {
        log.debug("Buscando cliente con ID: {}", id);//es la comunicacion con la base de datos, "lee", "machea" si ese id existe. ¿o me equivoco?: 
        Cliente cliente = buscarClienteOLanzarExcepcion(id);//funcion creada más abajo
        return mapearEntidadADto(cliente);//retorna otro return
    }

    @Override
    @Transactional(readOnly = true)//si "readOnly" es una "obligacion", ¿Qué es lo que hace por estar activado el "readOnly"?: 
    public ClienteResponseDTO obtenerClientePorEmail(String email) {
        log.debug("Se buscará cliente con email: {}", email);//se anota la accion que se va a realizar junto con el argumento usado para ell
        Cliente clientePorEmail = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNotFoundException("No se encontró cliente con email: " + email));
        return mapearEntidadADto(clientePorEmail);
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
                .stream()//.stream significa que convierte una lista a un flujo de datos. la traduccion de stream es:
                .map(this::mapearEntidadADto)
                .collect(Collectors.toList());//".collect", una traduccion sería: 
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
    }//relativamente sencillo de leer

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
