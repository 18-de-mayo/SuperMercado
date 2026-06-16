package duoc.cl.categoria.service;

import duoc.cl.categoria.dto.CategoriaDTO;
import duoc.cl.categoria.dto.CategoriaRequest;
import duoc.cl.categoria.exception.CategoriaNotFoundException;
import duoc.cl.categoria.model.CategoriaModel;
import duoc.cl.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// Capa de servicio — lógica de negocio del dominio CategoriaModel
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    // ── CREATE ──────────────────────────────────────────────────────

    public CategoriaDTO guardar(CategoriaRequest request) {
        // Regla de negocio: no se permiten categorías con nombre duplicado
        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(c -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con el nombre: " + request.getNombre());
        });

        CategoriaModel categoriaModel = new CategoriaModel();
        categoriaModel.setNombre(request.getNombre());
        categoriaModel.setDescripcion(request.getDescripcion());
        return mapToDTO(repository.save(categoriaModel));
    }

    // ── READ ─────────────────────────────────────────────────────────

    public List<CategoriaDTO> listar() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    // ── UPDATE ───────────────────────────────────────────────────────

    public CategoriaDTO actualizar(Long id, CategoriaRequest request) {
        CategoriaModel existente = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));

        // Verifica que el nuevo nombre no lo use otra categoría distinta
        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya existe otra categoría con el nombre: " + request.getNombre());
            }
        });

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        return mapToDTO(repository.save(existente));
    }

    // ── DELETE ───────────────────────────────────────────────────────

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ── MAPEO ────────────────────────────────────────────────────────

    private CategoriaDTO mapToDTO(CategoriaModel categoriaModel) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoriaModel.getId());
        dto.setNombre(categoriaModel.getNombre());
        dto.setDescripcion(categoriaModel.getDescripcion());
        return dto;
    }
}