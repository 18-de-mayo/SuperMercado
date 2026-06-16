package com.microservicio.cliente.repository;

import com.microservicio.cliente.model.Cliente;
import com.microservicio.cliente.model.Cliente.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones CRUD sobre la entidad Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Busca un cliente por su email */
    Optional<Cliente> findByEmail(String email);

    /** Busca un cliente por su RUT */
    Optional<Cliente> findByRut(String rut);

    /** Verifica si existe un cliente con el email dado */
    boolean existsByEmail(String email);

    /** Verifica si existe un cliente con el RUT dado */
    boolean existsByRut(String rut);

    /** Retorna todos los clientes con un estado específico */
    List<Cliente> findByEstado(EstadoCliente estado);

    /** Busca clientes por ciudad */
    List<Cliente> findByCiudadIgnoreCase(String ciudad);

    /**
     * Busca clientes cuyo nombre o apellido contenga el texto dado.
     * Útil para búsquedas parciales.
     */
    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Cliente> buscarPorNombreOApellido(String texto);
}
