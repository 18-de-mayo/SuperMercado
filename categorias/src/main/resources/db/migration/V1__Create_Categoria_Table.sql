-- Tabla de categorías del supermercado
-- Ejemplos: Lácteos, Bebidas, Carnes, Frutas y Verduras, etc.
CREATE TABLE categorias (
                            id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nombre      VARCHAR(100) NOT NULL,
                            descripcion VARCHAR(255),

                            CONSTRAINT uk_categorias_nombre UNIQUE (nombre)
);