package duoc.cl.despacho.service;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.dto.ProveedorDTO;
import duoc.cl.despacho.exception.DespachoNotFoundException;
import duoc.cl.despacho.feign.PedidoFeignClient;
import duoc.cl.despacho.feign.ProveedorFeignClient;
import duoc.cl.despacho.model.Despacho;
import duoc.cl.despacho.repository.DespachoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// Capa de servicio — lógica de negocio del dominio Despacho
@Service
@RequiredArgsConstructor
public class DespachoService {

    private final DespachoRepository repository;
    private final PedidoFeignClient pedidoFeignClient;
    private final ProveedorFeignClient proveedorFeignClient;

    // ── CREATE ──────────────────────────────────────────────────────

    public DespachoDTO guardar(DespachoRequest request) {
        // Valida que el pedido existe en MS pedido antes de crear el despacho
        try {
            pedidoFeignClient.obtenerPedido(request.getPedidoId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El pedido con ID " + request.getPedidoId() + " no existe o el MS pedido no está disponible.");
        }

        // Valida que el proveedor existe en MS proveedor
        try {
            proveedorFeignClient.obtenerProveedor(request.getProveedorId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El proveedor con ID " + request.getProveedorId() + " no existe o el MS proveedor no está disponible.");
        }

        Despacho despacho = new Despacho();
        despacho.setPedidoId(request.getPedidoId());
        despacho.setProveedorId(request.getProveedorId());
        despacho.setDireccionDestino(request.getDireccionDestino());
        despacho.setComuna(request.getComuna());

        return mapToDTO(repository.save(despacho));
    }

    // ── READ ─────────────────────────────────────────────────────────

    public List<DespachoDTO> listar() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DespachoDTO buscarPorId(Long id) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new DespachoNotFoundException(id));
        return mapToDTO(despacho);
    }

    // ── UPDATE ESTADO ────────────────────────────────────────────────

    // Avanza el estado del despacho: PENDIENTE → EN_RUTA → ENTREGADO
    public DespachoDTO actualizarEstado(Long id, String nuevoEstado) {
        Despacho despacho = repository.findById(id)
                .orElseThrow(() -> new DespachoNotFoundException(id));

        // Valida que la transición de estado sea válida
        validarTransicion(despacho.getEstado(), nuevoEstado);
        despacho.setEstado(nuevoEstado);

        return mapToDTO(repository.save(despacho));
    }

    // ── HELPERS ──────────────────────────────────────────────────────

    // Solo permite transiciones válidas entre estados
    private void validarTransicion(String estadoActual, String nuevoEstado) {
        boolean valido = switch (estadoActual) {
            case "PENDIENTE"  -> nuevoEstado.equals("EN_RUTA");
            case "EN_RUTA"    -> nuevoEstado.equals("ENTREGADO");
            case "ENTREGADO"  -> false; // estado terminal
            default -> false;
        };

        if (!valido) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Transición inválida: " + estadoActual + " → " + nuevoEstado +
                            ". Transiciones permitidas: PENDIENTE→EN_RUTA, EN_RUTA→ENTREGADO");
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

        // Intenta obtener nombre del proveedor desde MS proveedor
        // Si el servicio no está disponible, muestra "No disponible" sin romper la respuesta
        try {
            ProveedorDTO proveedor = proveedorFeignClient.obtenerProveedor(despacho.getProveedorId());
            dto.setNombreProveedor(proveedor != null ? proveedor.getNombre() : "No disponible");
        } catch (Exception e) {
            dto.setNombreProveedor("No disponible");
        }

        return dto;
    }
}