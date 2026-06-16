package duoc.cl.despacho.dto;

import lombok.Data;

// DTO de salida — datos que se retornan al consultar un despacho
@Data
public class DespachoDTO {
    private Long id;
    private Long pedidoId;
    private Long proveedorId;
    private String nombreProveedor;  // viene del MS proveedor via Feign
    private String estado;           // PENDIENTE | EN_RUTA | ENTREGADO
    private String direccionDestino;
    private String comuna;
}