package duoc.cl.despacho.repository;

import duoc.cl.despacho.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio JPA — operaciones CRUD sobre la tabla despachos
@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    // Verifica si ya existe un despacho para un pedido dado
    boolean existsByPedidoId(Long pedidoId);
}