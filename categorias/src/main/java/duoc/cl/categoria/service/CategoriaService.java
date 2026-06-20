package duoc.cl.categoria.service;

import duoc.cl.categoria.dto.CategoriaDTO;
import duoc.cl.categoria.dto.CategoriaRequest;
import duoc.cl.categoria.exception.CategoriaNotFoundException;
import duoc.cl.categoria.model.CategoriaModel;
import duoc.cl.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    // ── CREATE ──────────────────────────────────────────────────────
    @Transactional
    public CategoriaDTO guardar(CategoriaRequest request) {
        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(c -> {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.getNombre());
        });

        // Optimización: Uso limpio del patrón de diseño Builder
        CategoriaModel categoriaModel = CategoriaModel.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        return mapToDTO(repository.save(categoriaModel));
    }

    // ── READ ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar() {
        // Optimización Java 21: Reemplazo de Collectors.toList() por el .toList() nativo
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    // ── UPDATE ───────────────────────────────────────────────────────
    @Transactional
    public CategoriaDTO actualizar(Long id, CategoriaRequest request) {
        CategoriaModel existente = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));

        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(categoriaEncontrada -> {
            if (!categoriaEncontrada.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + request.getNombre());
            }
        });

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        return mapToDTO(repository.save(existente));
    }

    // ── DELETE ───────────────────────────────────────────────────────
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }
        repository.deleteById(id);
    }

    // ── MAPEO ────────────────────────────────────────────────────────
    private CategoriaDTO mapToDTO(CategoriaModel categoriaModel) {
        // Optimización: Uso limpio de Builder para la transferencia perimetral de salida
        return CategoriaDTO.builder()
                .id(categoriaModel.getId())
                .nombre(categoriaModel.getNombre())
                .descripcion(categoriaModel.getDescripcion())
                .build();
    }
}