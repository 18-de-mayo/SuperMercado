package cl.jorge.resena.repository;

import cl.jorge.resena.model.Resena;
import cl.jorge.resena.model.EstadoResena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    Optional<Resena> findByClienteIdAndProductoIdAndPedidoId(Long clienteId, Long productoId, Long pedidoId);

    // CORREGIDO: Debe devolver List<Resena>, no List<String>
    List<Resena> findByClienteId(Long clienteId);

    List<Resena> findByProductoId(Long productoId);

    List<Resena> findByProductoIdAndEstado(Long productoId, EstadoResena estado);

    List<Resena> findByEstado(EstadoResena estado);

    long countByProductoIdAndEstado(Long productoId, EstadoResena estado);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.productoId = :productoId AND r.estado = 'APROBADA'")
    Double calcularPromedioCalificacion(@Param("productoId") Long productoId);
}