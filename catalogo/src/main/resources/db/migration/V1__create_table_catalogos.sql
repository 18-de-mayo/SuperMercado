CREATE TABLE catalogo_campanas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_campana VARCHAR(255) NOT NULL
);

CREATE TABLE catalogo_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    precio_catalogo DECIMAL(12,2) NOT NULL,
    precio_oferta DECIMAL(12,2) NOT NULL,
    campana_id BIGINT,
    CONSTRAINT fk_catalogo_campana FOREIGN KEY (campana_id) REFERENCES catalogo_campanas(id) ON DELETE CASCADE
);