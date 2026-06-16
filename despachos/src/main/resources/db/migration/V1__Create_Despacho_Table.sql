-- Tabla de despachos — registra el envío físico de un pedido
CREATE TABLE despachos (
                           id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                           pedido_id        BIGINT       NOT NULL,
                           proveedor_id     BIGINT       NOT NULL,
                           direccion_destino VARCHAR(255) NOT NULL,
                           comuna           VARCHAR(100) NOT NULL,
                           estado           VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
                           fecha_creacion   DATETIME     NOT NULL,

                           CONSTRAINT chk_despacho_estado CHECK (estado IN ('PENDIENTE', 'EN_RUTA', 'ENTREGADO'))
);