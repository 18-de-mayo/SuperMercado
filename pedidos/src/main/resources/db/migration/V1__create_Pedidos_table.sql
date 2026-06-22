-- Tabla Principal: Pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha_pedido VARCHAR(255) NOT NULL,
    estado_pedido VARCHAR(255) NOT NULL
);

-- Nueva Tabla: Detalle Pedidos
CREATE TABLE detalle_pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,

    -- Esto crea la relación a nivel de Base de Datos para que coincida con tu @ManyToOne
    CONSTRAINT fk_detalle_pedidos_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id) ON DELETE CASCADE
);