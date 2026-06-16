-- ============================================================
-- V1__Create_Pago_Tables.sql
-- Migración inicial: Creación de tablas del dominio de Pagos
-- Microservicio: pago-microservicio
-- Base de datos: pago-db
-- ============================================================

-- Tabla de métodos de pago disponibles en el supermercado
CREATE TABLE metodos_pago (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    activo      BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT uk_metodos_pago_nombre UNIQUE (nombre)
);

-- Tabla principal de pagos (boletas/facturas) del supermercado
-- Cada pago se asocia a un pedido y a un cliente (IDs remotos)
CREATE TABLE pagos (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_boleta   VARCHAR(20)    NOT NULL,
    cliente_id      BIGINT         NOT NULL,
    pedido_id       BIGINT         NOT NULL,
    metodo_pago_id  BIGINT         NOT NULL,
    monto_total     DECIMAL(12, 2) NOT NULL,
    monto_descuento DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    monto_final     DECIMAL(12, 2) NOT NULL,
    estado          VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago      DATETIME       NOT NULL,
    observacion     VARCHAR(500),

    CONSTRAINT uk_pagos_numero_boleta UNIQUE (numero_boleta),
    CONSTRAINT uk_pagos_pedido_id     UNIQUE (pedido_id),
    CONSTRAINT fk_pagos_metodo        FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(id)
);

-- ── Datos semilla: métodos de pago iniciales ──────────────────────────────────
INSERT INTO metodos_pago (nombre, descripcion, activo) VALUES
    ('EFECTIVO',          'Pago en efectivo en caja',                       TRUE),
    ('DEBITO',            'Tarjeta de débito (Redcompra)',                  TRUE),
    ('CREDITO',           'Tarjeta de crédito (Visa/Mastercard/Amex)',      TRUE),
    ('TRANSFERENCIA',     'Transferencia bancaria electrónica',             TRUE),
    ('MONEDERO_VIRTUAL',  'Saldo del monedero virtual del cliente',         TRUE),
    ('VALE_DESCUENTO',    'Vale o cupón de descuento emitido por el local', TRUE);
