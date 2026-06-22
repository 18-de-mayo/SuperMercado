package duoc.cl.despacho.service;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.dto.ProveedorDTO;
import duoc.cl.despacho.exception.DespachoNotFoundException;
import duoc.cl.despacho.feign.PedidoFeignClient;
import duoc.cl.despacho.feign.ProveedorFeignClient;
import duoc.cl.despacho.model.Despacho;
import duoc.cl.despacho.model.EstadoDespacho;
import duoc.cl.despacho.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespachoService {

    // Instanciación del Logger para trazabilidad operacional (IE 3.3.6)
    private static final Logger log = LoggerFactory.getLogger(DespachoService.class);

    private final DespachoRepository repository;
    private final PedidoFeignClient pedidoFeignClient;
    private final ProveedorFeignClient proveedorFeignClient;

    // ── CREATE ──────────────────────────────────────────────────────

    public DespachoDTO guardar(DespachoRequest request) {
        log.info("Iniciando proceso de creación de despacho para el pedido ID: {}", request.getPedidoId());

        // Valida que el pedido existe en MS pedido antes de crear el despacho
        try {
            log.debug("Validando existencia del pedido ID: {} de manera remota", request.getPedidoId());
            pedidoFeignClient.obtenerPedido(request.getPedidoId());
        } catch (Exception e) {
            log.error("Error de validación remota: El pedido ID {} no existe o el servicio no responde.", request.getPedidoId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El pedido con ID " + request.getPedidoId() + " no existe o el MS pedido no está disponible.");
        }

        // Valida que el proveedor existe en MS proveedor
        try {
            log.debug("Validando existencia del proveedor ID: {} de manera remota", request.getProveedorId());
            proveedorFeignClient.obtenerProveedor(request.getProveedorId());
        } catch (Exception e) {
            log.error("Error de validación remota: El proveedor ID {} no existe o el servicio no responde.", request.getProveedorId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El proveedor con ID " + request.getProveedorId() + " no existe o el MS proveedor no está disponible.");
        }

        Despacho despacho = new Despacho();
        despacho.setPedidoId(request.getPedidoId());
        despacho.setProveedorId(request.getProveedorId());
        despacho.setDireccionDestino(request.getDireccionDestino());
        despacho.setComuna(request.getComuna());

        Despacho despachoGuardado = repository.save(despacho);
        log.info("Despacho creado exitosamente en Base de Datos con ID generado: {}", despachoGuardado.getId());

        return mapToDTO(despachoGuardado);
    }

    // ── READ ─────────────────────────────────────────────────────────

    public List<DespachoDTO> listar() {
        log.info("Solicitando listado completo de despachos");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DespachoDTO buscarPorId(Long id) {
        log.info("Buscando despacho por ID: {}", id);
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Búsqueda fallida: No se encontró el despacho con ID: {}", id);
                    return new DespachoNotFoundException(id);
                });
        return mapToDTO(despacho);
    }

    // ── UPDATE ESTADO ────────────────────────────────────────────────

    public DespachoDTO actualizarEstado(Long id, EstadoDespacho nuevoEstado) {
        log.info("Solicitud de cambio de estado para despacho ID: {} hacia el estado: {}", id, nuevoEstado);

        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Actualización fallida: No se encontró el despacho con ID: {}", id);
                    return new DespachoNotFoundException(id);
                });

        // Valida que la transición de estado sea válida
        validarTransicion(despacho.getEstado(), nuevoEstado);

        EstadoDespacho estadoAnterior = despacho.getEstado();
        despacho.setEstado(nuevoEstado);
        Despacho despachoActualizado = repository.save(despacho);

        log.info("Transición de estado exitosa para despacho ID {}: {} -> {}", id, estadoAnterior, nuevoEstado);

        return mapToDTO(despachoActualizado);
    }

    // ── HELPERS ──────────────────────────────────────────────────────

    private void validarTransicion(EstadoDespacho estadoActual, EstadoDespacho nuevoEstado) {
        boolean valido = switch (estadoActual) {
            case PENDIENTE -> nuevoEstado == EstadoDespacho.EN_RUTA;
            case EN_RUTA   -> nuevoEstado == EstadoDespacho.ENTREGADO;
            case ENTREGADO -> false;
        };

        if (!valido) {
            log.warn("Se rechazó una transición de estado ilegal: {} -> {}", estadoActual, nuevoEstado);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Transición inválida: " + estadoActual + " -> " + nuevoEstado +
                            ". Transiciones permitidas: PENDIENTE->EN_RUTA, EN_RUTA->ENTREGADO");
        }
    }

    private DespachoDTO mapToDTO(Despacho despacho) {
        DespachoDTO dto = new DespachoDTO();
        dto.setId(despacho.getId());
        dto.setPedidoId(despacho.getPedidoId());
        dto.setProveedorId(despacho.getProveedorId());
        dto.setEstado(despacho.getEstado());
        dto.setDireccionDestino(despacho.getDireccionDestino());
        dto.setComuna(despacho.getComuna());

        try {
            log.debug("Consultando nombre del proveedor ID: {} en MS proveedor para enriquecer el DTO", despacho.getProveedorId());
            ProveedorDTO proveedor = proveedorFeignClient.obtenerProveedor(despacho.getProveedorId());
            dto.setNombreProveedor(proveedor != null ? proveedor.getNombre() : "No disponible");
        } catch (Exception e) {
            log.warn("El MS proveedor no está disponible para el ID {}. Se asignará 'No disponible' al DTO.", despacho.getProveedorId());
            dto.setNombreProveedor("No disponible");
        }

        return dto;
    }
}