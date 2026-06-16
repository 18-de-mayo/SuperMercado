package cl.jorge.resena.service;

import cl.jorge.resena.client.ClienteClient;
import cl.jorge.resena.client.PagoClient;
import cl.jorge.resena.client.PedidoClient;
import cl.jorge.resena.client.ProductoClient;
import cl.jorge.resena.dto.*;
import cl.jorge.resena.exception.DuplicateResourceException;
import cl.jorge.resena.exception.EstadoInvalidoException;
import cl.jorge.resena.exception.ResourceNotFoundException;
import cl.jorge.resena.model.EstadoResena;
import cl.jorge.resena.model.Resena;
import cl.jorge.resena.model.RespuestaResena;
import cl.jorge.resena.repository.ResenaRepository;
import cl.jorge.resena.repository.RespuestaResenaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final RespuestaResenaRepository respuestaResenaRepository;

    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;
    private final PedidoClient pedidoClient;
    private final PagoClient pagoClient;

    // ──────────────────────────── CREATE ────────────────────────────

    @Transactional
    public ResenaDTO crearResena(ResenaRequest request) {
        log.info("[SERVICE] Iniciando creacion de resena para clienteId={}, productoId={}, pedidoId={}",
                request.getClienteId(), request.getProductoId(), request.getPedidoId());

        try {
            try {
                clienteClient.obtenerClientePorId(request.getClienteId());
            } catch (Exception e) {
                log.error("[SERVICE] Error al validar cliente ID {}: {}", request.getClienteId(), e.getMessage());
                throw new ResourceNotFoundException("El cliente con ID " + request.getClienteId() + " no existe.");
            }

            try {
                productoClient.obtenerProductoPorId(request.getProductoId());
            } catch (Exception e) {
                log.error("[SERVICE] Error al validar producto ID {}: {}", request.getProductoId(), e.getMessage());
                throw new ResourceNotFoundException("El producto con ID " + request.getProductoId() + " no existe.");
            }

            try {
                // CORREGIDO: Se mantiene tu método original del cliente pero enviando el pedidoId String
                pedidoClient.obtenerPedidoPorId(request.getPedidoId());
            } catch (Exception e) {
                log.error("[SERVICE] Error al validar pedido ID {}: {}", request.getPedidoId(), e.getMessage());
                throw new ResourceNotFoundException("El pedido con ID " + request.getPedidoId() + " no existe.");
            }

            resenaRepository.findByClienteIdAndProductoIdAndPedidoId(
                    request.getClienteId(), request.getProductoId(), request.getPedidoId()
            ).ifPresent(r -> {
                throw new DuplicateResourceException("El cliente ya publico una resena para este producto en el mismo pedido.");
            });

            Resena resena = new Resena();
            resena.setClienteId(request.getClienteId());
            resena.setProductoId(request.getProductoId());
            resena.setPedidoId(request.getPedidoId());
            resena.setCalificacion(request.getCalificacion());
            resena.setTitulo(request.getTitulo());
            resena.setComentario(request.getComentario());
            resena.setEstado(EstadoResena.PENDIENTE);
            resena.setFechaCreacion(LocalDateTime.now());

            Resena resenaGuardada = resenaRepository.save(resena);
            return mapearADto(resenaGuardada);

        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Error inesperado al crear resena: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo registrar la resena debido a un fallo interno.");
        }
    }

    // ──────────────────────────── READ ────────────────────────────

    @Transactional(readOnly = true)
    public List<ResenaDTO> obtenerTodas() {
        try {
            return resenaRepository.findAll()
                    .stream()
                    .map(this::mapearADto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[SERVICE] Fallo al listar resenas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al recuperar el listado de resenas.");
        }
    }

    @Transactional(readOnly = true)
    public ResenaDTO obtenerPorId(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con ID: " + id));
        return mapearADto(resena);
    }

    @Transactional(readOnly = true)
    public List<ResenaDTO> obtenerPorCliente(Long clienteId) {
        return resenaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResenaDTO> obtenerPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId)
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResenaDTO> obtenerResenaAprobadasPorProducto(Long productoId) {
        return resenaRepository.findByProductoIdAndEstado(productoId, EstadoResena.APROBADA)
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResenaDTO> obtenerPorEstado(EstadoResena estado) {
        return resenaRepository.findByEstado(estado)
                .stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    // ──────────────────────────── UPDATE ────────────────────────────

    @Transactional
    public ResenaDTO actualizarResena(Long id, ResenaRequest request) {
        try {
            Resena resena = resenaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con ID: " + id));

            if (resena.getEstado() != EstadoResena.PENDIENTE) {
                throw new EstadoInvalidoException("Solo se pueden editar resenas en estado PENDIENTE.");
            }

            resena.setCalificacion(request.getCalificacion());
            resena.setTitulo(request.getTitulo());
            resena.setComentario(request.getComentario());
            resena.setFechaEdicion(LocalDateTime.now());

            Resena resenaActualizada = resenaRepository.save(resena);
            return mapearADto(resenaActualizada);

        } catch (ResourceNotFoundException | EstadoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Error al actualizar resena ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("No se pudo actualizar la resena.");
        }
    }

    @Transactional
    public ResenaDTO actualizarEstado(Long id, ActualizarEstadoRequest request) {
        try {
            Resena resena = resenaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con ID: " + id));

            EstadoResena estadoActual = resena.getEstado();
            EstadoResena nuevoEstado = request.getNuevoEstado();

            if (!estadoActual.puedeTransicionarA(nuevoEstado)) {
                throw new EstadoInvalidoException("Transicion no permitida: " + estadoActual + " -> " + nuevoEstado);
            }

            resena.setEstado(nuevoEstado);
            Resena resenaActualizada = resenaRepository.save(resena);
            return mapearADto(resenaActualizada);

        } catch (ResourceNotFoundException | EstadoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Error al actualizar estado de resena ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("No se pudo actualizar el estado de la resena.");
        }
    }

    // ──────────────────────────── DELETE ────────────────────────────

    @Transactional
    public void eliminarResena(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con ID: " + id));
        resenaRepository.delete(resena);
    }

    // ──────────────────────────── RESPUESTAS ────────────────────────────

    @Transactional
    public RespuestaResenaDTO agregarRespuesta(Long resenaId, RespuestaResenaRequest request) {
        try {
            Resena resena = resenaRepository.findById(resenaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con ID: " + resenaId));

            if (resena.getEstado() != EstadoResena.APROBADA) {
                throw new EstadoInvalidoException("Solo se pueden responder resenas en estado APROBADA.");
            }

            RespuestaResena respuesta = new RespuestaResena();
            // CORREGIDO: Se removió por completo la línea basura text/xml que rompía la compilación
            respuesta.setResena(resena);
            respuesta.setAutor(request.getAutor());
            respuesta.setContenido(request.getContenido());
            respuesta.setFechaCreacion(LocalDateTime.now());

            RespuestaResena respuestaGuardada = respuestaResenaRepository.save(respuesta);
            return mapearRespuestaADto(respuestaGuardada);

        } catch (ResourceNotFoundException | EstadoInvalidoException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SERVICE] Error al agregar respuesta a resena ID {}: {}", resenaId, e.getMessage(), e);
            throw new RuntimeException("No se pudo agregar la respuesta.");
        }
    }

    @Transactional(readOnly = true)
    public List<RespuestaResenaDTO> obtenerRespuestasPorResena(Long resenaId) {
        resenaRepository.findById(resenaId)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada con ID: " + resenaId));

        return respuestaResenaRepository.findByResenaId(resenaId)
                .stream()
                .map(this::mapearRespuestaADto)
                .collect(Collectors.toList());
    }

    // ──────────────────────────── INTEGRACION DISTRIBUIDA ────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumenProducto(Long productoId) {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("productoId", productoId);

        List<ResenaDTO> resenasAprobadas = obtenerResenaAprobadasPorProducto(productoId);
        Double promedio = resenaRepository.calcularPromedioCalificacion(productoId);
        long totalAprobadas = resenaRepository.countByProductoIdAndEstado(productoId, EstadoResena.APROBADA);

        resumen.put("resenasAprobadas", resenasAprobadas);
        resumen.put("promedioCalificacion", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0);
        resumen.put("totalResenasAprobadas", totalAprobadas);

        try {
            ProductoResumenDTO producto = productoClient.obtenerProductoPorId(productoId);
            resumen.put("producto", producto);
        } catch (Exception e) {
            log.error("[SERVICE] Error al comunicar con producto-service: {}", e.getMessage());
            resumen.put("producto", "Servicio de productos no disponible temporalmente");
        }

        return resumen;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumenCliente(Long clienteId) {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("clienteId", clienteId);

        List<ResenaDTO> resenasCliente = obtenerPorCliente(clienteId);
        resumen.put("resenasPublicadas", resenasCliente);
        resumen.put("totalResenas", resenasCliente.size());

        try {
            ClienteResumenDTO cliente = clienteClient.obtenerClientePorId(clienteId);
            resumen.put("cliente", cliente);
        } catch (Exception e) {
            log.error("[SERVICE] Error al comunicar con cliente-service: {}", e.getMessage());
            resumen.put("cliente", "Servicio de clientes no disponible temporalmente");
        }

        try {
            Object pagos = pagoClient.obtenerPagosPorCliente(clienteId);
            resumen.put("pagos", pagos);
        } catch (Exception e) {
            log.error("[SERVICE] Error al comunicar con pago-service: {}", e.getMessage());
            resumen.put("pagos", "Servicio de pagos no disponible temporalmente");
        }

        return resumen;
    }

    // ──────────────────────────── MAPEO Y AGREGACION ────────────────────────────

    private ResenaDTO mapearADto(Resena resena) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(resena.getId());
        dto.setClienteId(resena.getClienteId());
        dto.setProductoId(resena.getProductoId());
        dto.setPedidoId(resena.getPedidoId()); // Ahora compila perfectamente String -> String
        dto.setCalificacion(resena.getCalificacion());
        dto.setTitulo(resena.getTitulo());
        dto.setComentario(resena.getComentario());
        dto.setEstado(resena.getEstado());
        dto.setFechaCreacion(resena.getFechaCreacion());
        dto.setFechaEdicion(resena.getFechaEdicion());

        if (resena.getRespuestas() != null) {
            dto.setRespuestas(resena.getRespuestas().stream()
                    .map(this::mapearRespuestaADto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private RespuestaResenaDTO mapearRespuestaADto(RespuestaResena respuesta) {
        RespuestaResenaDTO dto = new RespuestaResenaDTO();
        dto.setId(respuesta.getId());
        dto.setResenaId(respuesta.getResena().getId());
        dto.setAutor(respuesta.getAutor());
        dto.setContenido(respuesta.getContenido());
        dto.setFechaCreacion(respuesta.getFechaCreacion());
        return dto;
    }
}