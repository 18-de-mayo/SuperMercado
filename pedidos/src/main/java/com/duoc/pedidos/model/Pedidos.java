package com.duoc.pedidos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "pedidos")
@Schema(description = "Entidad que representa un pedido")
public class Pedidos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido", nullable = false, length = 20)
    private EstadoPedido estadoPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedidos> detalles;
}
