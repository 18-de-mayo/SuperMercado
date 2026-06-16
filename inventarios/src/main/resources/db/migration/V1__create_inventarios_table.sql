-- Tabla de inventarios — registra stock disponible y mínimo por producto
CREATE TABLE inventarios (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    producto_id         BIGINT       NOT NULL,
    stock_disponible    INT          NOT NULL,
    stock_minimo        INT          NOT NULL,
    fecha_actualizacion VARCHAR(50)  NOT NULL
);