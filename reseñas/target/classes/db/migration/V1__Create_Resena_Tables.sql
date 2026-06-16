-- ============================================================
-- V1__Create_Resena_Tables.sql
-- Migración inicial: Creación de tablas del microservicio de Reseñas
-- Microservicio: resena
-- ============================================================

-- Tabla principal de reseñas de productos publicadas por clientes
CREATE TABLE resenas (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id     BIGINT       NOT NULL COMMENT 'ID remoto del cliente (microservicio-cliente)',
    producto_id    BIGINT       NOT NULL COMMENT 'ID remoto del producto (microservicio-producto)',
    pedido_id      BIGINT       NOT NULL COMMENT 'ID remoto del pedido que originó la compra',
    calificacion   INT          NOT NULL COMMENT 'Puntuación del 1 al 5',
    titulo         VARCHAR(150) NOT NULL,
    comentario     TEXT         NOT NULL,
    estado         VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE | APROBADA | RECHAZADA',
    fecha_creacion DATETIME     NOT NULL,
    fecha_edicion  DATETIME     NULL,

    -- Un cliente solo puede reseñar el mismo producto de un mismo pedido una vez
    CONSTRAINT uk_resenas_cliente_producto_pedido UNIQUE (cliente_id, producto_id, pedido_id),

    CONSTRAINT chk_resenas_calificacion CHECK (calificacion BETWEEN 1 AND 5),
    CONSTRAINT chk_resenas_estado       CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA'))
);

-- Tabla de respuestas a reseñas (ej: respuesta oficial del supermercado)
CREATE TABLE respuestas_resena (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    resena_id      BIGINT       NOT NULL COMMENT 'FK a la reseña que se responde',
    autor          VARCHAR(100) NOT NULL COMMENT 'Nombre del respondedor (ej: Servicio al Cliente)',
    contenido      TEXT         NOT NULL,
    fecha_creacion DATETIME     NOT NULL,

    CONSTRAINT fk_respuestas_resena
        FOREIGN KEY (resena_id) REFERENCES resenas(id)
        ON DELETE CASCADE
);
