package com.microservicio.pago.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un pago en el sistema del supermercado.
 *
 * Reglas de negocio clave:
 * - Cada pedido solo puede tener un pago (restricción única sobre pedidoId).
 * - El número de recibo se genera automáticamente antes de persistir.
 * - El monto debe ser mayor a 0.
 * - Las transiciones de estado siguen un flujo controlado.
 */
@Entity
@Table(name = "pagos", uniqueConstraints = {
        @UniqueConstraint(columnNames = "pedido_id", name = "uk_pago_pedido")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del pedido asociado. Restricción única: 1 pago por pedido.
     */
    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    /**
     * ID del cliente que realiza el pago (obtenido del microservicio de pedidos).
     */
    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    /**
     * Número de recibo generado automáticamente. Formato: REC-YYYY-NNNNNN
     */
    @Column(name = "numero_recibo", nullable = false, unique = true, length = 20)
    private String numeroRecibo;

    /**
     * Monto total del pago. Debe ser mayor a 0.
     */
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El monto debe tener máximo 10 enteros y 2 decimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /**
     * Método de pago utilizado por el cliente.
     */
    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    /**
     * Estado actual del pago. Por defecto: PENDIENTE.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;

    /**
     * Notas opcionales del pago (referencia bancaria, voucher, etc.)
     */
    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
    @Column(length = 500)
    private String notas;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    /**
     * Genera el número de recibo y establece la fecha de creación antes de persistir.
     * El número de recibo tendrá formato temporal; el servicio lo reemplaza con el ID.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPago.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // ── Enums internos ──────────────────────────────────────────────────────

    public enum EstadoPago {
        /** Pago registrado pero no procesado aún */
        PENDIENTE,
        /** Pago procesado y confirmado exitosamente */
        COMPLETADO,
        /** Pago que falló durante el procesamiento */
        FALLIDO,
        /** Pago cancelado antes de ser procesado */
        CANCELADO,
        /** Pago completado que fue reembolsado al cliente */
        REEMBOLSADO
    }

    public enum MetodoPago {
        EFECTIVO,
        TARJETA_DEBITO,
        TARJETA_CREDITO,
        TRANSFERENCIA_BANCARIA,
        VALE_VISTA
    }
}
