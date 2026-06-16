package com.microservicio.pago.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO local que refleja la respuesta del microservicio de pedidos.
 * Solo contiene los campos que el microservicio de pagos necesita.
 */
@Data
@NoArgsConstructor
public class PedidoResponseDTO {

    private Long id;
    private Long clienteId;
    private BigDecimal total;
    private String estado;
}
