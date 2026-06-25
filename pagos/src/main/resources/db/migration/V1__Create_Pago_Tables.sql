CREATE TABLE pagos (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id           BIGINT        NOT NULL,
    cliente_id          BIGINT        NOT NULL,
    numero_recibo       VARCHAR(20)   NOT NULL,
    monto               DECIMAL(12,2) NOT NULL,
    metodo_pago         VARCHAR(30)   NOT NULL,
    estado              VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    notas               VARCHAR(500),
    fecha_creacion      DATETIME      NOT NULL,
    fecha_actualizacion DATETIME,
    fecha_pago          DATETIME,

    CONSTRAINT uk_pagos_pedido_id    UNIQUE (pedido_id),
    CONSTRAINT uk_pagos_numero_recibo UNIQUE (numero_recibo)
);
