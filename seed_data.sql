-- ==========================================================
-- SEED DATA — Sistema de Gestión Veterinaria (PostgreSQL / Neon)
-- Idempotente: se puede ejecutar varias veces sin duplicar.
-- Compatible con esquema_veterinaria.sql (sin medicamentos ni TI).
-- ==========================================================

BEGIN;

-- ==========================================================
-- ROLES
-- ==========================================================
INSERT INTO rol (nombre, descripcion, estado, created_at, updated_at) VALUES
('ADMIN', 'Administrador del sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VETERINARIO', 'Médico veterinario', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ESTILISTA', 'Estilista canino', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RECEPCIONISTA', 'Recepcionista', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CLIENTE', 'Cliente dueño de mascotas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- ==========================================================
-- MÓDULOS
-- ==========================================================
INSERT INTO modulo (nombre, descripcion, estado, created_at, updated_at) VALUES
('CLIENTES', 'Gestión de clientes', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MASCOTAS', 'Gestión de mascotas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CITAS', 'Gestión de citas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('FACTURACION', 'Facturación y pagos', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('HISTORIAL', 'Historial médico', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('USUARIOS', 'Gestión de usuarios', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLES', 'Gestión de roles y permisos', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TARIFAS', 'Gestión de tarifas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CALIFICACIONES', 'Calificaciones', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- ==========================================================
-- PERMISOS POR ROL (rol_modulo)
-- ==========================================================
INSERT INTO rol_modulo (id_rol, id_modulo, estado, created_at, updated_at)
SELECT r.id_rol, m.id_modulo, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM rol r
CROSS JOIN modulo m
WHERE r.nombre = 'ADMIN'
ON CONFLICT (id_rol, id_modulo) DO NOTHING;

INSERT INTO rol_modulo (id_rol, id_modulo, estado, created_at, updated_at)
SELECT r.id_rol, m.id_modulo, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM rol r
JOIN modulo m ON m.nombre IN ('MASCOTAS', 'CITAS', 'HISTORIAL')
WHERE r.nombre IN ('VETERINARIO', 'ESTILISTA')
ON CONFLICT (id_rol, id_modulo) DO NOTHING;

INSERT INTO rol_modulo (id_rol, id_modulo, estado, created_at, updated_at)
SELECT r.id_rol, m.id_modulo, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM rol r
JOIN modulo m ON m.nombre IN ('CLIENTES', 'MASCOTAS', 'CITAS', 'FACTURACION')
WHERE r.nombre = 'RECEPCIONISTA'
ON CONFLICT (id_rol, id_modulo) DO NOTHING;

INSERT INTO rol_modulo (id_rol, id_modulo, estado, created_at, updated_at)
SELECT r.id_rol, m.id_modulo, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM rol r
JOIN modulo m ON m.nombre IN ('MASCOTAS', 'CITAS', 'FACTURACION', 'CALIFICACIONES')
WHERE r.nombre = 'CLIENTE'
ON CONFLICT (id_rol, id_modulo) DO NOTHING;

-- ==========================================================
-- CARGOS
-- ==========================================================
INSERT INTO cargo (nombre, descripcion, estado, created_at, updated_at) VALUES
('VETERINARIO', 'Médico veterinario', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ESTILISTA', 'Estilista', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('RECEPCIONISTA', 'Recepcionista', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ADMINISTRADOR', 'Administrador', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- ==========================================================
-- SERVICIOS
-- ==========================================================
INSERT INTO servicio (nombre, descripcion, tipo_servicio, precio, estado, created_at, updated_at) VALUES
('Consulta General', 'Consulta médica general', 'CONSULTA', 50000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Consulta Especializada', 'Consulta médica especializada', 'CONSULTA', 80000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Vacunación', 'Aplicación de vacunas', 'CONSULTA', 35000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Baño Medicado', 'Baño con productos medicinales', 'ESTETICA', 40000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Corte de Pelo', 'Corte y arreglo de pelo', 'ESTETICA', 35000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Limpieza Dental', 'Limpieza dental', 'ESTETICA', 60000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cirugía Menor', 'Procedimiento quirúrgico menor', 'OTRO', 150000, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- ==========================================================
-- MÉTODOS DE PAGO
-- ==========================================================
INSERT INTO metodo_pago (nombre, descripcion, estado, created_at, updated_at) VALUES
('EFECTIVO', 'Pago en efectivo', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TARJETA_DEBITO', 'Tarjeta débito', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TARJETA_CREDITO', 'Tarjeta crédito', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('TRANSFERENCIA', 'Transferencia bancaria', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

-- ==========================================================
-- USUARIO ADMINISTRADOR
-- (credenciales: admin / admin123)
-- ==========================================================
INSERT INTO cargo (nombre, descripcion, estado, created_at, updated_at) VALUES
('ADMINISTRADOR', 'Administrador', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO empleado (
    id_cargo, tipo_documento, numero_documento,
    primer_nombre, primer_apellido, correo, telefono, direccion,
    fecha_ingreso, estado, created_at, updated_at
)
SELECT c.id_cargo, 'CC', '0000000001', 'Admin', 'Sistema',
       'admin@veterinaria.com', '3000000000', 'Calle 1',
       CURRENT_DATE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM cargo c
WHERE c.nombre = 'ADMINISTRADOR'
ON CONFLICT DO NOTHING;

INSERT INTO usuario (
    id_empleado, nombre_usuario, password_hash,
    estado, created_at, updated_at
)
SELECT e.id_empleado, 'admin',
       '$2b$12$J.7CZp5r8p7FYd4Dh18TXeajuC11vOcrSyduGp9qI43II6rdFiIQG',
       TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM empleado e
WHERE e.numero_documento = '0000000001'
ON CONFLICT (nombre_usuario) DO NOTHING;

INSERT INTO usuario_rol (id_usuario, id_rol, estado, created_at, updated_at)
SELECT u.id_usuario, r.id_rol, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuario u, rol r
WHERE u.nombre_usuario = 'admin' AND r.nombre = 'ADMIN'
ON CONFLICT (id_usuario, id_rol) DO NOTHING;

COMMIT;
