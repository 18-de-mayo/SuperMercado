CREATE TABLE pedidos (
    id            BIGINT      AUTO_INCREMENT PRIMARY KEY,
    id_cliente    BIGINT      NOT NULL,
    fecha_pedido  DATETIME    NOT NULL,
    estado_pedido VARCHAR(20) NOT NULL,

    CONSTRAINT chk_pedido_estado CHECK (estado_pedido IN ('PENDIENTE', 'COMPLETADO', 'CANCELADO'))
);

CREATE TABLE detalle_pedidos (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    id_pedido        BIGINT         NOT NULL,
    id_producto      BIGINT         NOT NULL,
    cantidad         INT            NOT NULL,
    precio_unitario  DECIMAL(12,2)  NOT NULL,

    CONSTRAINT fk_detalle_pedidos_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE
);
