-- =====================================================================
--  SISTEMA BANCARIO - Esquema, procedimientos y datos semilla (MySQL 8)
--  Este script se ejecuta automaticamente la PRIMERA vez que arranca el
--  contenedor (carpeta /docker-entrypoint-initdb.d).
--  Base de datos: banco   |   Conecta con uni.database.AccesoDB
-- =====================================================================

CREATE DATABASE IF NOT EXISTS banco DEFAULT CHARACTER SET utf8mb4;
USE banco;

-- =====================================================================
--  TABLAS
-- =====================================================================

-- Contadores para codigos legibles (C0001, E0001, ...)
CREATE TABLE control (
    parametro VARCHAR(20) PRIMARY KEY,
    valor     INT NOT NULL DEFAULT 0
);

CREATE TABLE sucursal (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    direccion   VARCHAR(150),
    telefono    VARCHAR(20)
);

CREATE TABLE empleado (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,
    nombres     VARCHAR(80) NOT NULL,
    apellidos   VARCHAR(80) NOT NULL,
    email       VARCHAR(120),
    usuario     VARCHAR(40) NOT NULL UNIQUE,
    clave       VARCHAR(100) NOT NULL,
    rol         VARCHAR(20) NOT NULL,          -- ADMIN, CAJERO
    id_sucursal INT NULL,
    CONSTRAINT fk_empleado_sucursal
        FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal)
);

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    codigo     VARCHAR(10)  NOT NULL UNIQUE,
    dni        VARCHAR(15)  NOT NULL UNIQUE,
    nombres    VARCHAR(80)  NOT NULL,
    apellidos  VARCHAR(80)  NOT NULL,
    direccion  VARCHAR(150),
    telefono   VARCHAR(20),
    email      VARCHAR(120),
    usuario    VARCHAR(60)  UNIQUE,   -- credencial portal Banca Digital (= dni por defecto)
    clave      VARCHAR(100)           -- clave portal Banca Digital       (= dni por defecto)
);

CREATE TABLE tipocuenta (
    id_tipocuenta INT AUTO_INCREMENT PRIMARY KEY,
    codigo        VARCHAR(10) NOT NULL UNIQUE,
    nombre        VARCHAR(40) NOT NULL,
    tasa_interes  DECIMAL(6,4) NOT NULL DEFAULT 0
);

CREATE TABLE cuenta (
    numero         VARCHAR(14) PRIMARY KEY,
    id_cliente     INT NOT NULL,
    id_tipocuenta  INT NOT NULL,
    saldo          DECIMAL(14,2) NOT NULL DEFAULT 0,
    fecha_apertura DATE NOT NULL DEFAULT (CURRENT_DATE),
    estado         VARCHAR(15) NOT NULL DEFAULT 'ACTIVA',  -- ACTIVA, BLOQUEADA
    CONSTRAINT fk_cuenta_cliente
        FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_cuenta_tipo
        FOREIGN KEY (id_tipocuenta) REFERENCES tipocuenta(id_tipocuenta)
);

CREATE TABLE movimiento (
    id_movimiento     INT AUTO_INCREMENT PRIMARY KEY,
    codigo            VARCHAR(12) NOT NULL UNIQUE,
    numero_cuenta     VARCHAR(14) NOT NULL,
    tipo              VARCHAR(15) NOT NULL,    -- DEPOSITO, RETIRO, TRANSFERENCIA
    monto             DECIMAL(14,2) NOT NULL,
    fecha             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_empleado       INT NOT NULL,
    cuenta_relacionada VARCHAR(14) NULL,
    saldo_resultante  DECIMAL(14,2) NOT NULL,
    CONSTRAINT fk_mov_cuenta
        FOREIGN KEY (numero_cuenta) REFERENCES cuenta(numero),
    CONSTRAINT fk_mov_empleado
        FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado)
);

-- Auditoria de la demo de patrones (Observer/Decorator -> AuditoriaDAOImpl).
CREATE TABLE auditoria (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    usuario   VARCHAR(60) NOT NULL,
    operacion VARCHAR(40) NOT NULL,
    detalle   TEXT,
    fecha     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
--  PROCEDIMIENTOS ALMACENADOS
-- =====================================================================
DELIMITER $$

-- ---- Generador de codigos ----
CREATE PROCEDURE sp_siguiente_valor(IN p_param VARCHAR(20), OUT p_valor INT)
BEGIN
    UPDATE control SET valor = valor + 1 WHERE parametro = p_param;
    SELECT valor INTO p_valor FROM control WHERE parametro = p_param;
END$$

-- ---- SUCURSAL ----
CREATE PROCEDURE sp_sucursal_insert(IN p_codigo VARCHAR(10), IN p_nombre VARCHAR(100),
                                    IN p_direccion VARCHAR(150), IN p_telefono VARCHAR(20))
BEGIN
    INSERT INTO sucursal(codigo, nombre, direccion, telefono)
    VALUES (p_codigo, p_nombre, p_direccion, p_telefono);
END$$

CREATE PROCEDURE sp_sucursal_update(IN p_id INT, IN p_nombre VARCHAR(100),
                                    IN p_direccion VARCHAR(150), IN p_telefono VARCHAR(20))
BEGIN
    UPDATE sucursal SET nombre = p_nombre, direccion = p_direccion, telefono = p_telefono
    WHERE id_sucursal = p_id;
END$$

CREATE PROCEDURE sp_sucursal_delete(IN p_id INT)
BEGIN
    DELETE FROM sucursal WHERE id_sucursal = p_id;
END$$

-- ---- EMPLEADO ----
CREATE PROCEDURE sp_empleado_insert(IN p_codigo VARCHAR(10), IN p_nombres VARCHAR(80),
                                    IN p_apellidos VARCHAR(80), IN p_email VARCHAR(120),
                                    IN p_usuario VARCHAR(40), IN p_clave VARCHAR(100),
                                    IN p_rol VARCHAR(20), IN p_id_sucursal INT)
BEGIN
    INSERT INTO empleado(codigo, nombres, apellidos, email, usuario, clave, rol, id_sucursal)
    VALUES (p_codigo, p_nombres, p_apellidos, p_email, p_usuario, p_clave, p_rol, p_id_sucursal);
END$$

CREATE PROCEDURE sp_empleado_update(IN p_id INT, IN p_nombres VARCHAR(80),
                                    IN p_apellidos VARCHAR(80), IN p_email VARCHAR(120),
                                    IN p_usuario VARCHAR(40), IN p_clave VARCHAR(100),
                                    IN p_rol VARCHAR(20), IN p_id_sucursal INT)
BEGIN
    -- Si la clave llega vacia, se conserva la actual.
    UPDATE empleado
       SET nombres = p_nombres, apellidos = p_apellidos, email = p_email,
           usuario = p_usuario, rol = p_rol, id_sucursal = p_id_sucursal,
           clave = IF(p_clave IS NULL OR p_clave = '', clave, p_clave)
     WHERE id_empleado = p_id;
END$$

CREATE PROCEDURE sp_empleado_delete(IN p_id INT)
BEGIN
    DELETE FROM empleado WHERE id_empleado = p_id;
END$$

-- ---- CLIENTE ----
CREATE PROCEDURE sp_cliente_insert(IN p_codigo VARCHAR(10), IN p_dni VARCHAR(15),
                                   IN p_nombres VARCHAR(80), IN p_apellidos VARCHAR(80),
                                   IN p_direccion VARCHAR(150), IN p_telefono VARCHAR(20),
                                   IN p_email VARCHAR(120))
BEGIN
    -- usuario y clave se inicializan con el DNI; el cliente puede cambiarlos despues.
    INSERT INTO cliente(codigo,dni,nombres,apellidos,direccion,telefono,email,usuario,clave)
    VALUES (p_codigo,p_dni,p_nombres,p_apellidos,p_direccion,p_telefono,p_email,p_dni,p_dni);
END$$

CREATE PROCEDURE sp_cliente_update(IN p_id INT, IN p_dni VARCHAR(15),
                                   IN p_nombres VARCHAR(80), IN p_apellidos VARCHAR(80),
                                   IN p_direccion VARCHAR(150), IN p_telefono VARCHAR(20),
                                   IN p_email VARCHAR(120))
BEGIN
    UPDATE cliente
       SET dni = p_dni, nombres = p_nombres, apellidos = p_apellidos,
           direccion = p_direccion, telefono = p_telefono, email = p_email
     WHERE id_cliente = p_id;
END$$

CREATE PROCEDURE sp_cliente_delete(IN p_id INT)
BEGIN
    DELETE FROM cliente WHERE id_cliente = p_id;
END$$

-- ---- TIPOCUENTA ----
CREATE PROCEDURE sp_tipocuenta_insert(IN p_codigo VARCHAR(10), IN p_nombre VARCHAR(40),
                                      IN p_tasa DECIMAL(6,4))
BEGIN
    INSERT INTO tipocuenta(codigo, nombre, tasa_interes) VALUES (p_codigo, p_nombre, p_tasa);
END$$

CREATE PROCEDURE sp_tipocuenta_update(IN p_id INT, IN p_nombre VARCHAR(40), IN p_tasa DECIMAL(6,4))
BEGIN
    UPDATE tipocuenta SET nombre = p_nombre, tasa_interes = p_tasa WHERE id_tipocuenta = p_id;
END$$

CREATE PROCEDURE sp_tipocuenta_delete(IN p_id INT)
BEGIN
    DELETE FROM tipocuenta WHERE id_tipocuenta = p_id;
END$$

-- ---- CUENTA ----
CREATE PROCEDURE sp_cuenta_insert(IN p_numero VARCHAR(14), IN p_id_cliente INT,
                                  IN p_id_tipo INT, IN p_saldo DECIMAL(14,2),
                                  IN p_estado VARCHAR(15))
BEGIN
    INSERT INTO cuenta(numero, id_cliente, id_tipocuenta, saldo, estado)
    VALUES (p_numero, p_id_cliente, p_id_tipo, p_saldo, p_estado);
END$$

CREATE PROCEDURE sp_cuenta_update(IN p_numero VARCHAR(14), IN p_id_cliente INT,
                                  IN p_id_tipo INT, IN p_estado VARCHAR(15))
BEGIN
    UPDATE cuenta SET id_cliente = p_id_cliente, id_tipocuenta = p_id_tipo, estado = p_estado
    WHERE numero = p_numero;
END$$

CREATE PROCEDURE sp_cuenta_delete(IN p_numero VARCHAR(14))
BEGIN
    DELETE FROM cuenta WHERE numero = p_numero;
END$$

-- ---- OPERACIONES TRANSACCIONALES ----
-- Nota: la transaccion (autocommit off / commit / rollback) la controla el
-- DAO en Java. Estos procedimientos validan reglas y modifican filas; ante
-- error lanzan SIGNAL para que el DAO ejecute rollback.

CREATE PROCEDURE sp_deposito(IN p_numero VARCHAR(14), IN p_monto DECIMAL(14,2),
                             IN p_idemp INT, IN p_codigo VARCHAR(12))
BEGIN
    DECLARE v_saldo DECIMAL(14,2);
    DECLARE v_estado VARCHAR(15);

    SELECT saldo, estado INTO v_saldo, v_estado FROM cuenta WHERE numero = p_numero FOR UPDATE;
    IF v_saldo IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cuenta no existe.';
    END IF;
    IF v_estado <> 'ACTIVA' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cuenta no esta activa.';
    END IF;

    UPDATE cuenta SET saldo = saldo + p_monto WHERE numero = p_numero;
    INSERT INTO movimiento(codigo, numero_cuenta, tipo, monto, id_empleado, saldo_resultante)
    VALUES (p_codigo, p_numero, 'DEPOSITO', p_monto, p_idemp, v_saldo + p_monto);
END$$

CREATE PROCEDURE sp_retiro(IN p_numero VARCHAR(14), IN p_monto DECIMAL(14,2),
                           IN p_idemp INT, IN p_codigo VARCHAR(12))
BEGIN
    DECLARE v_saldo DECIMAL(14,2);
    DECLARE v_estado VARCHAR(15);

    SELECT saldo, estado INTO v_saldo, v_estado FROM cuenta WHERE numero = p_numero FOR UPDATE;
    IF v_saldo IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cuenta no existe.';
    END IF;
    IF v_estado <> 'ACTIVA' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La cuenta no esta activa.';
    END IF;
    IF v_saldo < p_monto THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Saldo insuficiente para el retiro.';
    END IF;

    UPDATE cuenta SET saldo = saldo - p_monto WHERE numero = p_numero;
    INSERT INTO movimiento(codigo, numero_cuenta, tipo, monto, id_empleado, saldo_resultante)
    VALUES (p_codigo, p_numero, 'RETIRO', p_monto, p_idemp, v_saldo - p_monto);
END$$

CREATE PROCEDURE sp_transferencia(IN p_origen VARCHAR(14), IN p_destino VARCHAR(14),
                                  IN p_monto DECIMAL(14,2), IN p_idemp INT,
                                  IN p_cod_origen VARCHAR(12), IN p_cod_destino VARCHAR(12))
BEGIN
    DECLARE v_saldo_o DECIMAL(14,2);
    DECLARE v_estado_o VARCHAR(15);
    DECLARE v_saldo_d DECIMAL(14,2);
    DECLARE v_estado_d VARCHAR(15);

    -- Bloquea ambas cuentas (orden por numero para evitar interbloqueos).
    SELECT saldo, estado INTO v_saldo_o, v_estado_o FROM cuenta WHERE numero = p_origen FOR UPDATE;
    SELECT saldo, estado INTO v_saldo_d, v_estado_d FROM cuenta WHERE numero = p_destino FOR UPDATE;

    IF v_saldo_o IS NULL OR v_saldo_d IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cuenta origen o destino inexistente.';
    END IF;
    IF v_estado_o <> 'ACTIVA' OR v_estado_d <> 'ACTIVA' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Alguna cuenta no esta activa.';
    END IF;
    IF v_saldo_o < p_monto THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Saldo insuficiente en la cuenta origen.';
    END IF;

    UPDATE cuenta SET saldo = saldo - p_monto WHERE numero = p_origen;
    UPDATE cuenta SET saldo = saldo + p_monto WHERE numero = p_destino;

    INSERT INTO movimiento(codigo, numero_cuenta, tipo, monto, id_empleado, cuenta_relacionada, saldo_resultante)
    VALUES (p_cod_origen, p_origen, 'TRANSFERENCIA', p_monto, p_idemp, p_destino, v_saldo_o - p_monto);
    INSERT INTO movimiento(codigo, numero_cuenta, tipo, monto, id_empleado, cuenta_relacionada, saldo_resultante)
    VALUES (p_cod_destino, p_destino, 'TRANSFERENCIA', p_monto, p_idemp, p_origen, v_saldo_d + p_monto);
END$$

DELIMITER ;

-- =====================================================================
--  DATOS SEMILLA (para que cada modulo sea demostrable de inmediato)
-- =====================================================================

-- Contadores iniciales coherentes con los registros sembrados.
INSERT INTO control(parametro, valor) VALUES
 ('SUCURSAL', 1), ('EMPLEADO', 1), ('CLIENTE', 3),
 ('TIPOCUENTA', 2), ('CUENTA', 4), ('MOVIMIENTO', 0);

INSERT INTO sucursal(codigo, nombre, direccion, telefono) VALUES
 ('S0001', 'Sucursal Principal', 'Av. Central 123', '01-5550000');

-- Empleado administrador: usuario = admin, clave = admin
INSERT INTO empleado(codigo, nombres, apellidos, email, usuario, clave, rol, id_sucursal) VALUES
 ('E0001', 'Administrador', 'del Sistema', 'admin@banco.com', 'admin', 'admin', 'ADMIN', 1);

INSERT INTO tipocuenta(codigo, nombre, tasa_interes) VALUES
 ('T0001', 'Ahorros', 0.0250),
 ('T0002', 'Corriente', 0.0000);

-- usuario y clave = DNI por defecto (el cliente puede cambiarlos)
INSERT INTO cliente(codigo,dni,nombres,apellidos,direccion,telefono,email,usuario,clave) VALUES
 ('C0001','70123456','Maria','Fernandez','Jr. Lima 456','987654321','maria@correo.com','70123456','70123456'),
 ('C0002','40876512','Jose', 'Ramirez',  'Av. Sol 789', '999111222','jose@correo.com', '40876512','40876512'),
 ('C0003','75409202','Andre','Quispe',   'Av. Arequipa 100','987001122','andre@correo.com','75409202','75409202');

INSERT INTO cuenta(numero, id_cliente, id_tipocuenta, saldo, estado) VALUES
 ('0000000001', 1, 1, 3000.00, 'ACTIVA'),   -- Maria: ahorros
 ('0000000002', 2, 1,  850.00, 'ACTIVA'),   -- Jose:  ahorros
 ('0000000003', 3, 1, 5000.00, 'ACTIVA'),   -- Andre: ahorros (cp transferencia propia)
 ('0000000004', 3, 2, 1500.00, 'ACTIVA');   -- Andre: corriente
