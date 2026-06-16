package cl.jorge.resena.repository;

import cl.jorge.resena.model.RespuestaResena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para operaciones CRUD sobre la entidad RespuestaResena.
 * IE 2.1.2: Conecta la capa de persistencia con los endpoints REST.
 */
@Repository
public interface RespuestaResenaRepository extends JpaRepository<RespuestaResena, Long> {

    /** Obtiene todas las respuestas asociadas a una reseña específica. */
    List<RespuestaResena> findByResenaId(Long resenaId);
}
