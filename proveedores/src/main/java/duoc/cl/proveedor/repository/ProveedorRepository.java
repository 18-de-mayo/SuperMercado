package duoc.cl.proveedor.repository;

import duoc.cl.proveedor.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    boolean existsByCorreo(String correo);

    boolean existsByRut(String rut);
}