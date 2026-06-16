-- Tabla de clientes del supermercado
CREATE TABLE clientes (
                          id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre    VARCHAR(100) NOT NULL,
                          rut       VARCHAR(20)  NOT NULL,
                          edad      INT          NOT NULL,
                          direccion VARCHAR(255) NOT NULL,
                          nivel     INT          NOT NULL,
                          monedero  DOUBLE       NOT NULL,

                          CONSTRAINT uk_clientes_rut UNIQUE (rut)
);