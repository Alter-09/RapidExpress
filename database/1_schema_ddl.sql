-- RapidExpress Fleet Management System - Database Schema
-- MySQL 8.x compatible (InnoDB, utf8mb4)
-- Fixed for CloudBeaver: proper delimiters, order, syntax

DELIMITER $$

-- ============================================================
-- 1. SETTINGS
-- ============================================================
SET NAMES utf8mb4$$
SET FOREIGN_KEY_CHECKS = 0$$

-- ============================================================
-- 2. TABLE: vehiculo (no dependencies)
-- ============================================================
DROP TABLE IF EXISTS `vehiculo`$$
CREATE TABLE `vehiculo` (
    `placa` VARCHAR(10) NOT NULL,
    `marca` VARCHAR(50) NOT NULL,
    `modelo` VARCHAR(50) NOT NULL,
    `anio_fabricacion` SMALLINT UNSIGNED NOT NULL,
    `capacidad_max_kg` DECIMAL(10,2) UNSIGNED NOT NULL,
    `estado` ENUM('Disponible','En_Ruta','En_Mantenimiento') NOT NULL DEFAULT 'Disponible',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`placa`),
    CONSTRAINT `chk_vehiculo_anio` CHECK (`anio_fabricacion` BETWEEN 1990 AND 2030),
    CONSTRAINT `chk_vehiculo_capacidad` CHECK (`capacidad_max_kg` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 3. TABLE: conductor (depends on vehiculo)
-- ============================================================
DROP TABLE IF EXISTS `conductor`$$
CREATE TABLE `conductor` (
    `num_identificacion` VARCHAR(20) NOT NULL,
    `nombre_completo` VARCHAR(100) NOT NULL,
    `tipo_licencia` ENUM('A','B','C','D','E') NOT NULL,
    `num_contacto` VARCHAR(20) NOT NULL,
    `estado` ENUM('Activo','De_Vacaciones','Inactivo','En_Ruta') NOT NULL DEFAULT 'Activo',
    `vehiculo_asignado` VARCHAR(10) DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`num_identificacion`),
    UNIQUE KEY `uk_conductor_vehiculo` (`vehiculo_asignado`),
    CONSTRAINT `fk_conductor_vehiculo` FOREIGN KEY (`vehiculo_asignado`) REFERENCES `vehiculo` (`placa`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 4. TABLE: paquete (no dependencies)
-- ============================================================
DROP TABLE IF EXISTS `paquete`$$
CREATE TABLE `paquete` (
    `id_seguimiento` VARCHAR(36) NOT NULL,
    `descripcion` VARCHAR(255) NOT NULL,
    `peso_kg` DECIMAL(8,2) UNSIGNED NOT NULL,
    `dimensiones` VARCHAR(50) NOT NULL COMMENT 'Format: LxWxH in cm',
    `dir_origen` VARCHAR(255) NOT NULL,
    `dir_destino` VARCHAR(255) NOT NULL,
    `remitente_nombre` VARCHAR(100) NOT NULL,
    `remitente_contacto` VARCHAR(20) NOT NULL,
    `remitente_direccion` VARCHAR(255) NOT NULL,
    `destinatario_nombre` VARCHAR(100) NOT NULL,
    `destinatario_contacto` VARCHAR(20) NOT NULL,
    `destinatario_direccion` VARCHAR(255) NOT NULL,
    `estado` ENUM('En_Bodega','Asignado_Ruta','En_Transito','Entregado','Devuelto') NOT NULL DEFAULT 'En_Bodega',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_seguimiento`),
    CONSTRAINT `chk_paquete_peso` CHECK (`peso_kg` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 5. TABLE: ruta (depends on vehiculo, conductor)
-- ============================================================
DROP TABLE IF EXISTS `ruta`$$
CREATE TABLE `ruta` (
    `id_ruta` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `fecha` DATE NOT NULL,
    `vehiculo_placa` VARCHAR(10) NOT NULL,
    `conductor_id` VARCHAR(20) NOT NULL,
    `estado` ENUM('Planificada','En_Curso','Completada','Cancelada') NOT NULL DEFAULT 'Planificada',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_ruta`),
    KEY `idx_ruta_fecha` (`fecha`),
    KEY `idx_ruta_vehiculo` (`vehiculo_placa`),
    KEY `idx_ruta_conductor` (`conductor_id`),
    CONSTRAINT `fk_ruta_vehiculo` FOREIGN KEY (`vehiculo_placa`) REFERENCES `vehiculo` (`placa`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_ruta_conductor` FOREIGN KEY (`conductor_id`) REFERENCES `conductor` (`num_identificacion`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 6. TABLE: ruta_paquete (depends on ruta, paquete)
-- ============================================================
DROP TABLE IF EXISTS `ruta_paquete`$$
CREATE TABLE `ruta_paquete` (
    `ruta_id` BIGINT UNSIGNED NOT NULL,
    `paquete_id` VARCHAR(36) NOT NULL,
    `estado_paquete` ENUM('Asignado','En_Transito','Entregado','Devuelto','No_Entregado') NOT NULL DEFAULT 'Asignado',
    `orden_entrega` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    `observaciones` TEXT DEFAULT NULL,
    `actualizado_en` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`ruta_id`, `paquete_id`),
    KEY `idx_ruta_paquete_paquete` (`paquete_id`),
    CONSTRAINT `fk_ruta_paquete_ruta` FOREIGN KEY (`ruta_id`) REFERENCES `ruta` (`id_ruta`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_ruta_paquete_paquete` FOREIGN KEY (`paquete_id`) REFERENCES `paquete` (`id_seguimiento`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 7. TABLE: mantenimiento (depends on vehiculo)
-- ============================================================
DROP TABLE IF EXISTS `mantenimiento`$$
CREATE TABLE `mantenimiento` (
    `id_mantenimiento` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `vehiculo_placa` VARCHAR(10) NOT NULL,
    `fecha` DATE NOT NULL,
    `tipo` ENUM('Preventivo','Correctivo','Inspeccion') NOT NULL,
    `descripcion` TEXT NOT NULL,
    `costo` DECIMAL(12,2) UNSIGNED NOT NULL DEFAULT 0,
    `kilometraje` INT UNSIGNED DEFAULT NULL,
    `proveedor` VARCHAR(100) DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id_mantenimiento`),
    KEY `idx_mantenimiento_vehiculo` (`vehiculo_placa`),
    KEY `idx_mantenimiento_fecha` (`fecha`),
    CONSTRAINT `fk_mantenimiento_vehiculo` FOREIGN KEY (`vehiculo_placa`) REFERENCES `vehiculo` (`placa`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `chk_mantenimiento_costo` CHECK (`costo` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 8. TABLE: auditoria (no dependencies)
-- ============================================================
DROP TABLE IF EXISTS `auditoria`$$
CREATE TABLE `auditoria` (
    `id_auditoria` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `timestamp` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `operacion` VARCHAR(50) NOT NULL COMMENT 'CREATE, UPDATE, DELETE, STATE_CHANGE, ASSIGN, UNASSIGN, START_ROUTE, END_ROUTE, etc.',
    `entidad` VARCHAR(50) NOT NULL COMMENT 'vehiculo, conductor, paquete, ruta, mantenimiento',
    `entidad_id` VARCHAR(100) NOT NULL COMMENT 'PK value of affected entity',
    `usuario` VARCHAR(100) DEFAULT NULL COMMENT 'Operator/user who performed the action',
    `detalles` JSON DEFAULT NULL COMMENT 'Old/new values, context data',
    `ip_origen` VARCHAR(45) DEFAULT NULL,
    PRIMARY KEY (`id_auditoria`),
    KEY `idx_auditoria_timestamp` (`timestamp`),
    KEY `idx_auditoria_entidad` (`entidad`, `entidad_id`),
    KEY `idx_auditoria_operacion` (`operacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci$$

-- ============================================================
-- 9. VIEW: v_ruta_detalle
-- ============================================================
DROP VIEW IF EXISTS `v_ruta_detalle`$$
CREATE VIEW `v_ruta_detalle` AS
SELECT
    r.`id_ruta`,
    r.`fecha`,
    r.`estado` AS ruta_estado,
    v.`placa` AS vehiculo_placa,
    v.`marca` AS vehiculo_marca,
    v.`modelo` AS vehiculo_modelo,
    v.`capacidad_max_kg`,
    c.`num_identificacion` AS conductor_id,
    c.`nombre_completo` AS conductor_nombre,
    c.`tipo_licencia`,
    COUNT(rp.`paquete_id`) AS total_paquetes,
    SUM(CASE WHEN rp.`estado_paquete` = 'Entregado' THEN 1 ELSE 0 END) AS paquetes_entregados,
    SUM(CASE WHEN rp.`estado_paquete` = 'Devuelto' THEN 1 ELSE 0 END) AS paquetes_devueltos,
    SUM(CASE WHEN rp.`estado_paquete` = 'No_Entregado' THEN 1 ELSE 0 END) AS paquetes_no_entregados,
    COALESCE(SUM(p.`peso_kg`), 0) AS peso_total_kg
FROM `ruta` r
JOIN `vehiculo` v ON r.`vehiculo_placa` = v.`placa`
JOIN `conductor` c ON r.`conductor_id` = c.`num_identificacion`
LEFT JOIN `ruta_paquete` rp ON r.`id_ruta` = rp.`ruta_id`
LEFT JOIN `paquete` p ON rp.`paquete_id` = p.`id_seguimiento`
GROUP BY r.`id_ruta`$$

-- ============================================================
-- 10. VIEW: v_vehiculo_estado
-- ============================================================
DROP VIEW IF EXISTS `v_vehiculo_estado`$$
CREATE VIEW `v_vehiculo_estado` AS
SELECT
    v.`placa`,
    v.`marca`,
    v.`modelo`,
    v.`anio_fabricacion`,
    v.`capacidad_max_kg`,
    v.`estado`,
    c.`num_identificacion` AS conductor_id,
    c.`nombre_completo` AS conductor_nombre,
    c.`estado` AS conductor_estado,
    (
        SELECT COUNT(*) FROM `ruta` r
        WHERE r.`vehiculo_placa` = v.`placa` AND r.`estado` = 'En_Curso'
    ) AS rutas_activas,
    (
        SELECT COUNT(*) FROM `mantenimiento` m
        WHERE m.`vehiculo_placa` = v.`placa` AND m.`fecha` >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
    ) AS mantenimientos_6m
FROM `vehiculo` v
LEFT JOIN `conductor` c ON v.`placa` = c.`vehiculo_asignado`$$

-- ============================================================
-- 11. RESTORE SETTINGS
-- ============================================================
SET FOREIGN_KEY_CHECKS = 1$$

DELIMITER ;