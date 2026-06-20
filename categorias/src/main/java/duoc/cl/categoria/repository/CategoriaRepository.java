package duoc.cl.categoria.repository;

import duoc.cl.categoria.model.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repositorio JPA — operaciones CRUD sobre la tabla categorias
@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {

    // Busca categoría por nombre — para validar duplicados
    Optional<CategoriaModel> findByNombreIgnoreCase(String nombre);
}