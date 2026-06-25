CREATE TABLE clientes (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,
    apellido            VARCHAR(100) NOT NULL,
    rut                 VARCHAR(12)  NOT NULL,
    email               VARCHAR(150) NOT NULL,
    telefono            VARCHAR(20)  NOT NULL,
    direccion           VARCHAR(255) NOT NULL,
    ciudad              VARCHAR(100) NOT NULL,
    region              VARCHAR(100) NOT NULL,
    fecha_nacimiento    DATE,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    fecha_registro      DATETIME     NOT NULL,
    fecha_actualizacion DATETIME,

    CONSTRAINT uk_clientes_rut   UNIQUE (rut),
    CONSTRAINT uk_clientes_email UNIQUE (email)
);
