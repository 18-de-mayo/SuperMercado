CREATE TABLE inventarios (
    id                   BIGINT    AUTO_INCREMENT PRIMARY KEY,
    producto_id          BIGINT    NOT NULL,
    stock_disponible     INT       NOT NULL,
    stock_minimo         INT       NOT NULL,
    fecha_actualizacion  DATETIME  NOT NULL,

    CONSTRAINT uk_inventarios_producto UNIQUE (producto_id)
);
