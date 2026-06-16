package duoc.cl.productos.repository;

import duoc.cl.productos.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

    // validar si existe un producto
    boolean existsByNombre(String nombre);

    // listar productos con stock
    List<Producto> findByCantidadGreaterThan(Integer cantidad);

    // buscar por nombre
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}