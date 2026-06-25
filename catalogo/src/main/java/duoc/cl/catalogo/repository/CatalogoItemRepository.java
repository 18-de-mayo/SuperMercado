package duoc.cl.catalogo.repository;

import duoc.cl.catalogo.model.CatalogoItem;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CatalogoItemRepository
        extends JpaRepository<CatalogoItem, Long> {
    Optional<CatalogoItem> findByProductoId(Long productoId);
    boolean existsByCampanaIdAndProductoId(Long campanaId, Long productoId);
}