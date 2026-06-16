package duoc.cl.catalogo.repository;

import duoc.cl.catalogo.model.CatalogoCampana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogoCampanaRepository extends JpaRepository<CatalogoCampana, Long> {
}