CREATE TABLE productos (

    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    precio DOUBLE,
    cantidad INT,
    proveedor_id BIGINT,
    categoria VARCHAR(100)
);