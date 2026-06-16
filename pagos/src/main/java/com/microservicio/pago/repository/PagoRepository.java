package com.microservicio.pago.repository;

import com.microservicio.pago.model.Pago;
import com.microservicio.pago.model.Pago.EstadoPago;
import com.microservicio.pago.model.Pago.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones CRUD sobre la entidad Pago.
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /** Busca el pago asociado a un pedido específico */
    Optional<Pago> findByPedidoId(Long pedidoId);

    /** Verifica si ya existe un pago para un pedido dado (regla 1:1) */
    boolean existsByPedidoId(Long pedidoId);

    /** Lista todos los pagos de un cliente específico */
    List<Pago> findByClienteId(Long clienteId);

    /** Lista los pagos en un estado determinado */
    List<Pago> findByEstado(EstadoPago estado);

    /** Lista los pagos realizados con un método de pago específico */
    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    /** Busca un pago por su número de recibo único */
    Optional<Pago> findByNumeroRecibo(String numeroRecibo);

    /** Lista pagos de un cliente en un estado específico */
    List<Pago> findByClienteIdAndEstado(Long clienteId, EstadoPago estado);

    /**
     * Suma el total de montos de pagos COMPLETADOS entre dos fechas.
     * Útil para reportes y auditoría financiera.
     */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "WHERE p.estado = 'COMPLETADO' " +
           "AND p.fechaCreacion BETWEEN :desde AND :hasta")
    BigDecimal sumarMontosCompletadosEntreFechas(LocalDateTime desde, LocalDateTime hasta);

    /** Cuenta cuántos pagos tiene un cliente en un estado dado */
    long countByClienteIdAndEstado(Long clienteId, EstadoPago estado);
}
