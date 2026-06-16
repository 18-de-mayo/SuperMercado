package com.duoc.inventarios.service;

import com.duoc.inventarios.client.ProductoClient;
import com.duoc.inventarios.dto.InventarioDTO;
import com.duoc.inventarios.dto.InventariosRequest;
import com.duoc.inventarios.exception.InventarioNotFoundException;
import com.duoc.inventarios.exception.ProductoNotFoundException;
import com.duoc.inventarios.model.Inventarios;
import com.duoc.inventarios.repository.InventariosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Capa de servicio — lógica de negocio del dominio Inventario
@Slf4j
@Service
public class InventariosService {

    @Autowired
    private InventariosRepository inventariosRepository;

    @Autowired
    private ProductoClient productoClient;

    // ── CREATE ──────────────────────────────────────────────────────

    public InventarioDTO crearInventario(InventariosRequest request) {
        // Valida que el producto existe en MS producto antes de crear el inventario
        validarProducto(request.getProductoId());

        Inventarios inventario = new Inventarios();
        inventario.setProductoId(request.getProductoId());
        inventario.setStockDisponible(request.getStockDisponible());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setFechaActualizacion(request.getFechaActualizacion());

        log.info("Inventarion creado correctamente: " + inventario);
        return convertirADTO(inventariosRepository.save(inventario));
    }

    // ── READ ─────────────────────────────────────────────────────────

    public List<InventarioDTO> obtenerInventarios() {
        return inventariosRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public InventarioDTO buscarInventarioPorId(Integer id) {
        Inventarios inventario = inventariosRepository.findById(id)
                .orElseThrow(() -> new InventarioNotFoundException(id));
        return convertirADTO(inventario);
    }

    // ── UPDATE ───────────────────────────────────────────────────────

    public InventarioDTO actualizarInventario(Integer id, InventariosRequest request) {
        // Valida que el producto existe antes de actualizar
        validarProducto(request.getProductoId());

        Inventarios existente = inventariosRepository.findById(id)
                .orElseThrow(() -> new InventarioNotFoundException(id));

        existente.setProductoId(request.getProductoId());
        existente.setStockDisponible(request.getStockDisponible());
        existente.setStockMinimo(request.getStockMinimo());
        existente.setFechaActualizacion(request.getFechaActualizacion());

        return convertirADTO(inventariosRepository.save(existente));
    }

    // ── DELETE ───────────────────────────────────────────────────────

    public void eliminarInventario(Integer id) {
        inventariosRepository.findById(id)
                .orElseThrow(() -> new InventarioNotFoundException(id));
        inventariosRepository.deleteById(id);
    }

    // ── HELPERS ──────────────────────────────────────────────────────

    // Llama al MS producto por ID — lanza excepción si no existe o el servicio no responde
    private void validarProducto(Long productoId) {
        try {
            productoClient.obtenerProductoPorId(productoId);
        } catch (Exception e) {
            throw new ProductoNotFoundException(productoId.intValue());
        }
    }

    private InventarioDTO convertirADTO(Inventarios inventario) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(inventario.getId());
        dto.setProductoId(inventario.getProductoId());
        dto.setStockDisponible(inventario.getStockDisponible());
        dto.setStockMinimo(inventario.getStockMinimo());
        dto.setFechaActualizacion(inventario.getFechaActualizacion());
        return dto;
    }
}