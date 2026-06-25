package duoc.cl.categoria.service;

import duoc.cl.categoria.dto.CategoriaDTO;
import duoc.cl.categoria.dto.CategoriaRequest;
import duoc.cl.categoria.exception.CategoriaNotFoundException;
import duoc.cl.categoria.model.CategoriaModel;
import duoc.cl.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    // ── CREATE ──────────────────────────────────────────────────────
    @Transactional
    public CategoriaDTO guardar(CategoriaRequest request) {
        log.info("Guardando nueva categoría: {}", request.getNombre());
        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(c -> {
            log.warn("Intento de crear categoría duplicada: {}", request.getNombre());
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.getNombre());
        });

        CategoriaModel categoriaModel = CategoriaModel.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();

        CategoriaDTO guardada = mapToDTO(repository.save(categoriaModel));
        log.info("Categoría guardada exitosamente con ID: {}", guardada.getId());
        return guardada;
    }

    // ── READ ─────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar() {
        log.info("Listando todas las categorías");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Long id) {
        log.info("Buscando categoría por ID: {}", id);
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
    }

    // ── UPDATE ───────────────────────────────────────────────────────
    @Transactional
    public CategoriaDTO actualizar(Long id, CategoriaRequest request) {
        log.info("Actualizando categoría ID: {}", id);
        CategoriaModel existente = repository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));

        repository.findByNombreIgnoreCase(request.getNombre()).ifPresent(categoriaEncontrada -> {
            if (!categoriaEncontrada.getId().equals(id)) {
                log.warn("Intento de actualizar a nombre duplicado: {}", request.getNombre());
                throw new IllegalArgumentException("Ya existe otra categoría con el nombre: " + request.getNombre());
            }
        });

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        CategoriaDTO actualizada = mapToDTO(repository.save(existente));
        log.info("Categoría ID {} actualizada exitosamente", id);
        return actualizada;
    }

    // ── DELETE ───────────────────────────────────────────────────────
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando categoría ID: {}", id);
        if (!repository.existsById(id)) {
            throw new CategoriaNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Categoría ID {} eliminada exitosamente", id);
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