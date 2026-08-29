-- RapidExpress Fleet Management System - Sample Data (DML)
-- MySQL 8.x compatible
-- ≥20 records per main entity: vehiculo, conductor, paquete, ruta, mantenimiento

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- VEHICULOS (25 records)
-- ============================================================
INSERT INTO `vehiculo` (`placa`, `marca`, `modelo`, `anio_fabricacion`, `capacidad_max_kg`, `estado`) VALUES
('ABC-123', 'Chevrolet', 'NPR 700', 2020, 5000.00, 'Disponible'),
('ABC-124', 'Chevrolet', 'NPR 700', 2021, 5000.00, 'Disponible'),
('ABC-125', 'Ford', 'F-650', 2019, 7000.00, 'En_Ruta'),
('ABC-126', 'Ford', 'F-650', 2020, 7000.00, 'Disponible'),
('ABC-127', 'Volvo', 'FH 540', 2022, 18000.00, 'Disponible'),
('ABC-128', 'Volvo', 'FH 540', 2021, 18000.00, 'En_Mantenimiento'),
('ABC-129', 'Mercedes-Benz', 'Atego 1725', 2020, 10000.00, 'Disponible'),
('ABC-130', 'Mercedes-Benz', 'Atego 1725', 2019, 10000.00, 'Disponible'),
('ABC-131', 'Scania', 'P 360', 2022, 16000.00, 'Disponible'),
('ABC-132', 'Scania', 'P 360', 2021, 16000.00, 'En_Ruta'),
('ABC-133', 'International', 'MV 607', 2020, 8000.00, 'Disponible'),
('ABC-134', 'International', 'MV 607', 2019, 8000.00, 'Disponible'),
('ABC-135', 'Kenworth', 'T680', 2022, 20000.00, 'Disponible'),
('ABC-136', 'Kenworth', 'T680', 2021, 20000.00, 'En_Ruta'),
('ABC-137', 'Peterbilt', '579', 2020, 22000.00, 'Disponible'),
('ABC-138', 'Peterbilt', '579', 2019, 22000.00, 'Disponible'),
('ABC-139', 'Freightliner', 'Cascadia', 2022, 19000.00, 'Disponible'),
('ABC-140', 'Freightliner', 'Cascadia', 2021, 19000.00, 'En_Mantenimiento'),
('ABC-141', 'MAN', 'TGX 18.440', 2020, 15000.00, 'Disponible'),
('ABC-142', 'MAN', 'TGX 18.440', 2019, 15000.00, 'Disponible'),
('ABC-143', 'Iveco', 'S-Way 480', 2022, 17000.00, 'Disponible'),
('ABC-144', 'Iveco', 'S-Way 480', 2021, 17000.00, 'En_Ruta'),
('ABC-145', 'DAF', 'XF 450', 2020, 14000.00, 'Disponible'),
('ABC-146', 'DAF', 'XF 450', 2019, 14000.00, 'Disponible'),
('ABC-147', 'Renault', 'T 480', 2022, 13000.00, 'Disponible');

-- ============================================================
-- CONDUCTORES (25 records)
-- ============================================================
INSERT INTO `conductor` (`num_identificacion`, `nombre_completo`, `tipo_licencia`, `num_contacto`, `estado`, `vehiculo_asignado`) VALUES
('1000000001', 'Carlos Andrés Rodríguez', 'C', '3001112233', 'Activo', 'ABC-123'),
('1000000002', 'María Fernanda Gómez', 'C', '3001112244', 'Activo', 'ABC-124'),
('1000000003', 'Juan Carlos Martínez', 'E', '3001112255', 'Activo', 'ABC-125'),
('1000000004', 'Ana Lucía Hernández', 'C', '3001112266', 'Activo', 'ABC-126'),
('1000000005', 'Luis Alberto Torres', 'E', '3001112277', 'Activo', 'ABC-127'),
('1000000006', 'Patricia Elena Vargas', 'D', '3001112288', 'De_Vacaciones', 'ABC-128'),
('1000000007', 'Roberto Carlos Jiménez', 'C', '3001112299', 'Activo', 'ABC-129'),
('1000000008', 'Sandra Milena Ruiz', 'C', '3001112300', 'Activo', 'ABC-130'),
('1000000009', 'Andrés Felipe Morales', 'E', '3001112311', 'Activo', 'ABC-131'),
('1000000010', 'Claudia Patricia Ortiz', 'E', '3001112322', 'Activo', 'ABC-132'),
('1000000011', 'Jorge Iván Herrera', 'C', '3001112333', 'Activo', 'ABC-133'),
('1000000012', 'Laura Cristina Medina', 'C', '3001112344', 'Activo', 'ABC-134'),
('1000000013', 'Fernando José Castro', 'E', '3001112355', 'Activo', 'ABC-135'),
('1000000014', 'Mónica Alejandra Ramos', 'E', '3001112366', 'Activo', 'ABC-136'),
('1000000015', 'Ricardo Antonio Flores', 'C', '3001112377', 'Activo', 'ABC-137'),
('1000000016', 'Diana Carolina Acosta', 'C', '3001112388', 'Activo', 'ABC-138'),
('1000000017', 'Eduardo Javier Peña', 'E', '3001112399', 'Activo', 'ABC-139'),
('1000000018', 'Natalia Andrea Soto', 'E', '3001112400', 'Inactivo', 'ABC-140'),
('1000000019', 'Diego Armando Cruz', 'C', '3001112411', 'Activo', 'ABC-141'),
('1000000020', 'Paula Andrea Mendoza', 'C', '3001112422', 'Activo', 'ABC-142'),
('1000000021', 'Santiago José Vargas', 'E', '3001112433', 'Activo', 'ABC-143'),
('1000000022', 'Valentina Ríos', 'E', '3001112444', 'Activo', 'ABC-144'),
('1000000023', 'Sebastián Gutiérrez', 'C', '3001112455', 'Activo', 'ABC-145'),
('1000000024', 'Isabela Córdoba', 'C', '3001112466', 'Activo', 'ABC-146'),
('1000000025', 'Alejandro Peña', 'E', '3001112477', 'Activo', 'ABC-147');

-- ============================================================
-- PAQUETES (30 records)
-- ============================================================
INSERT INTO `paquete` (`id_seguimiento`, `descripcion`, `peso_kg`, `dimensiones`, `dir_origen`, `dir_destino`, `remitente_nombre`, `remitente_contacto`, `remitente_direccion`, `destinatario_nombre`, `destinatario_contacto`, `destinatario_direccion`, `estado`) VALUES
('PKG-20240115-001', 'Documentos legales', 2.50, '30x20x5', 'Calle 100 #15-20, Bogotá', 'Carrera 50 #25-30, Medellín', 'LegalTech SAS', '3102001001', 'Calle 100 #15-20, Bogotá', 'Abogados Asociados', '3102001002', 'Carrera 50 #25-30, Medellín', 'En_Bodega'),
('PKG-20240115-002', 'Ropa deportiva', 15.00, '50x40x30', 'Av. El Dorado #68-95, Bogotá', 'Calle 30 #45-10, Cali', 'Deportes Colombia', '3102001003', 'Av. El Dorado #68-95, Bogotá', 'Tienda Sport Cali', '3102001004', 'Calle 30 #45-10, Cali', 'En_Bodega'),
('PKG-20240115-003', 'Electrónicos', 8.75, '40x30x20', 'Zona Franca, Cartagena', 'Av. 80 #12-45, Barranquilla', 'TechImport Ltda', '3102001005', 'Zona Franca, Cartagena', 'ElectroBarranquilla', '3102001006', 'Av. 80 #12-45, Barranquilla', 'En_Bodega'),
('PKG-20240115-004', 'Medicamentos', 5.20, '25x20x15', 'Calle 26 #59-15, Bogotá', 'Carrera 15 #30-25, Bucaramanga', 'FarmaDistribuidor', '3102001007', 'Calle 26 #59-15, Bogotá', 'Droguería Central', '3102001008', 'Carrera 15 #30-25, Bucaramanga', 'En_Bodega'),
('PKG-20240115-005', 'Autopartes', 45.00, '80x60x40', 'Av. Américas #45-20, Medellín', 'Calle 50 #20-10, Pereira', 'AutoPartes Medellín', '3102001009', 'Av. Américas #45-20, Medellín', 'Taller Pereira', '3102001010', 'Calle 50 #20-10, Pereira', 'En_Bodega'),
('PKG-20240115-006', 'Libros escolares', 25.00, '60x40x35', 'Carrera 7 #32-16, Bogotá', 'Av. Circunvalar #25-30, Cúcuta', 'Editorial Santillana', '3102001011', 'Carrera 7 #32-16, Bogotá', 'Colegio San José', '3102001012', 'Av. Circunvalar #25-30, Cúcuta', 'En_Bodega'),
('PKG-20240115-007', 'Alimentos no perecederos', 120.00, '100x80x60', 'Zona Industrial, Cali', 'Calle 12 #15-20, Popayán', 'Alimentos del Valle', '3102001013', 'Zona Industrial, Cali', 'Supermercado Popayán', '3102001014', 'Calle 12 #15-20, Popayán', 'En_Bodega'),
('PKG-20240115-008', 'Muebles desarmados', 80.00, '120x80x50', 'Av. 68 #40-25, Bogotá', 'Carrera 27 #45-10, Manizales', 'Muebles Modernos', '3102001015', 'Av. 68 #40-25, Bogotá', 'Hogar Manizales', '3102001016', 'Carrera 27 #45-10, Manizales', 'En_Bodega'),
('PKG-20240115-009', 'Productos de belleza', 12.50, '35x25x20', 'Calle 85 #15-30, Bogotá', 'Av. 30 #20-15, Ibagué', 'Belleza Natural', '3102001017', 'Calle 85 #15-30, Bogotá', 'Salón Ibagué', '3102001018', 'Av. 30 #20-15, Ibagué', 'En_Bodega'),
('PKG-20240115-010', 'Herramientas industriales', 65.00, '90x60x45', 'Zona Franca, Barranquilla', 'Calle 40 #10-25, Santa Marta', 'Herramientas del Caribe', '3102001019', 'Zona Franca, Barranquilla', 'Ferretería Santa Marta', '3102001020', 'Calle 40 #10-25, Santa Marta', 'En_Bodega'),
('PKG-20240116-001', 'Ropa de bebé', 8.00, '45x35x25', 'Calle 100 #15-20, Bogotá', 'Carrera 50 #25-30, Medellín', 'BabyModa', '3102001021', 'Calle 100 #15-20, Bogotá', 'Bebé Feliz Medellín', '3102001022', 'Carrera 50 #25-30, Medellín', 'En_Bodega'),
('PKG-20240116-002', 'Suplementos vitamínicos', 3.50, '20x15x10', 'Av. El Dorado #68-95, Bogotá', 'Calle 30 #45-10, Cali', 'VitaSuplementos', '3102001023', 'Av. El Dorado #68-95, Bogotá', 'Farmacia Cali', '3102001024', 'Calle 30 #45-10, Cali', 'En_Bodega'),
('PKG-20240116-003', 'Juguetes educativos', 18.00, '55x45x35', 'Zona Franca, Cartagena', 'Av. 80 #12-45, Barranquilla', 'Juguetes y Más', '3102001025', 'Zona Franca, Cartagena', 'Tienda Juguetes BAQ', '3102001026', 'Av. 80 #12-45, Barranquilla', 'En_Bodega'),
('PKG-20240116-004', 'Café tostado', 30.00, '60x40x30', 'Calle 26 #59-15, Bogotá', 'Carrera 15 #30-25, Bucaramanga', 'Café Colombia', '3102001027', 'Calle 26 #59-15, Bogotá', 'Cafetería Bucaramanga', '3102001028', 'Carrera 15 #30-25, Bucaramanga', 'En_Bodega'),
('PKG-20240116-005', 'Material de construcción', 200.00, '150x100x80', 'Av. Américas #45-20, Medellín', 'Calle 50 #20-10, Pereira', 'ConstruMateriales', '3102001029', 'Av. Américas #45-20, Medellín', 'Ferretería Pereira', '3102001030', 'Calle 50 #20-10, Pereira', 'En_Bodega'),
('PKG-20240116-006', 'Artículos de oficina', 22.00, '50x40x30', 'Carrera 7 #32-16, Bogotá', 'Av. Circunvalar #25-30, Cúcuta', 'OficinaTotal', '3102001031', 'Carrera 7 #32-16, Bogotá', 'Empresa Cúcuta', '3102001032', 'Av. Circunvalar #25-30, Cúcuta', 'En_Bodega'),
('PKG-20240116-007', 'Frutas deshidratadas', 10.00, '40x30x20', 'Zona Industrial, Cali', 'Calle 12 #15-20, Popayán', 'Frutos del Valle', '3102001033', 'Zona Industrial, Cali', 'Natural Popayán', '3102001034', 'Calle 12 #15-20, Popayán', 'En_Bodega'),
('PKG-20240116-008', 'Bicicletas plegables', 55.00, '110x70x40', 'Av. 68 #40-25, Bogotá', 'Carrera 27 #45-10, Manizales', 'BiciUrbana', '3102001035', 'Av. 68 #40-25, Bogotá', 'Ciclo Manizales', '3102001036', 'Carrera 27 #45-10, Manizales', 'En_Bodega'),
('PKG-20240116-009', 'Perfumes', 4.00, '25x20x15', 'Calle 85 #15-30, Bogotá', 'Av. 30 #20-15, Ibagué', 'Fragancias Finas', '3102001037', 'Calle 85 #15-30, Bogotá', 'Perfumería Ibagué', '3102001038', 'Av. 30 #20-15, Ibagué', 'En_Bodega'),
('PKG-20240116-010', 'Equipos de cómputo', 35.00, '70x50x40', 'Zona Franca, Barranquilla', 'Calle 40 #10-25, Santa Marta', 'CompuZone', '3102001039', 'Zona Franca, Barranquilla', 'Tech Santa Marta', '3102001040', 'Calle 40 #10-25, Santa Marta', 'En_Bodega'),
('PKG-20240117-001', 'Zapatos deportivos', 12.00, '35x25x15', 'Calle 100 #15-20, Bogotá', 'Carrera 50 #25-30, Medellín', 'Calzado Activo', '3102001041', 'Calle 100 #15-20, Bogotá', 'Deportes Medellín', '3102001042', 'Carrera 50 #25-30, Medellín', 'En_Bodega'),
('PKG-20240117-002', 'Productos de limpieza', 18.00, '50x35x25', 'Av. El Dorado #68-95, Bogotá', 'Calle 30 #45-10, Cali', 'Limpieza Total', '3102001043', 'Av. El Dorado #68-95, Bogotá', 'Supermercado Cali', '3102001044', 'Calle 30 #45-10, Cali', 'En_Bodega'),
('PKG-20240117-003', 'Artículos para mascotas', 20.00, '55x40x30', 'Zona Franca, Cartagena', 'Av. 80 #12-45, Barranquilla', 'Mundo Mascotas', '3102001045', 'Zona Franca, Cartagena', 'PetShop Barranquilla', '3102001046', 'Av. 80 #12-45, Barranquilla', 'En_Bodega'),
('PKG-20240117-004', 'Instrumentos musicales', 25.00, '80x40x30', 'Calle 26 #59-15, Bogotá', 'Carrera 15 #30-25, Bucaramanga', 'Música y Arte', '3102001047', 'Calle 26 #59-15, Bogotá', 'Academia Bucaramanga', '3102001048', 'Carrera 15 #30-25, Bucaramanga', 'En_Bodega'),
('PKG-20240117-005', 'Ropa de cama', 15.00, '60x50x35', 'Av. Américas #45-20, Medellín', 'Calle 50 #20-10, Pereira', 'Hogar Dulce Hogar', '3102001049', 'Av. Américas #45-20, Medellín', 'Textiles Pereira', '3102001050', 'Calle 50 #20-10, Pereira', 'En_Bodega'),
('PKG-20240117-006', 'Baterías y acumuladores', 8.00, '30x20x15', 'Carrera 7 #32-16, Bogotá', 'Av. Circunvalar #25-30, Cúcuta', 'Energía Móvil', '3102001051', 'Carrera 7 #32-16, Bogotá', 'Electrónica Cúcuta', '3102001052', 'Av. Circunvalar #25-30, Cúcuta', 'En_Bodega'),
('PKG-20240117-007', 'Aceites esenciales', 2.00, '15x10x8', 'Zona Industrial, Cali', 'Calle 12 #15-20, Popayán', 'Aromaterapia', '3102001053', 'Zona Industrial, Cali', 'Spa Popayán', '3102001054', 'Calle 12 #15-20, Popayán', 'En_Bodega'),
('PKG-20240117-008', 'Cámaras de seguridad', 6.00, '25x20x15', 'Av. 68 #40-25, Bogotá', 'Carrera 27 #45-10, Manizales', 'Seguridad Total', '3102001055', 'Av. 68 #40-25, Bogotá', 'Oficinas Manizales', '3102001056', 'Carrera 27 #45-10, Manizales', 'En_Bodega'),
('PKG-20240117-009', 'Suplementos proteicos', 5.00, '20x15x10', 'Calle 85 #15-30, Bogotá', 'Av. 30 #20-15, Ibagué', 'FitNutrition', '3102001057', 'Calle 85 #15-30, Bogotá', 'Gimnasio Ibagué', '3102001058', 'Av. 30 #20-15, Ibagué', 'En_Bodega'),
('PKG-20240117-010', 'Drone profesional', 3.00, '30x30x15', 'Zona Franca, Barranquilla', 'Calle 40 #10-25, Santa Marta', 'DronTech', '3102001059', 'Zona Franca, Barranquilla', 'Fotografía Santa Marta', '3102001060', 'Calle 40 #10-25, Santa Marta', 'En_Bodega');

-- ============================================================
-- RUTAS (10 records)
-- ============================================================
INSERT INTO `ruta` (`id_ruta`, `fecha`, `vehiculo_placa`, `conductor_id`, `estado`) VALUES
(1, '2024-01-15', 'ABC-125', '1000000003', 'En_Curso'),
(2, '2024-01-15', 'ABC-132', '1000000010', 'Planificada'),
(3, '2024-01-16', 'ABC-136', '1000000014', 'Planificada'),
(4, '2024-01-16', 'ABC-144', '1000000022', 'En_Curso'),
(5, '2024-01-17', 'ABC-123', '1000000001', 'Planificada'),
(6, '2024-01-17', 'ABC-124', '1000000002', 'Planificada'),
(7, '2024-01-18', 'ABC-126', '1000000004', 'Planificada'),
(8, '2024-01-18', 'ABC-127', '1000000005', 'Planificada'),
(9, '2024-01-19', 'ABC-129', '1000000007', 'Planificada'),
(10, '2024-01-19', 'ABC-130', '1000000008', 'Planificada');

-- ============================================================
-- RUTA_PAQUETE (assign packages to routes)
-- ============================================================
INSERT INTO `ruta_paquete` (`ruta_id`, `paquete_id`, `estado_paquete`, `orden_entrega`) VALUES
-- Ruta 1 (ABC-125, 2024-01-15) - 5 packages
(1, 'PKG-20240115-001', 'Entregado', 1),
(1, 'PKG-20240115-002', 'Entregado', 2),
(1, 'PKG-20240115-003', 'En_Transito', 3),
(1, 'PKG-20240115-004', 'Asignado', 4),
(1, 'PKG-20240115-005', 'Asignado', 5),
-- Ruta 2 (ABC-132, 2024-01-15) - 4 packages
(2, 'PKG-20240115-006', 'Asignado', 1),
(2, 'PKG-20240115-007', 'Asignado', 2),
(2, 'PKG-20240115-008', 'Asignado', 3),
(2, 'PKG-20240115-009', 'Asignado', 4),
-- Ruta 3 (ABC-136, 2024-01-16) - 3 packages
(3, 'PKG-20240115-010', 'Asignado', 1),
(3, 'PKG-20240116-001', 'Asignado', 2),
(3, 'PKG-20240116-002', 'Asignado', 3),
-- Ruta 4 (ABC-144, 2024-01-16) - 4 packages
(4, 'PKG-20240116-003', 'Entregado', 1),
(4, 'PKG-20240116-004', 'En_Transito', 2),
(4, 'PKG-20240116-005', 'Asignado', 3),
(4, 'PKG-20240116-006', 'Asignado', 4),
-- Ruta 5 (ABC-123, 2024-01-17) - 3 packages
(5, 'PKG-20240116-007', 'Asignado', 1),
(5, 'PKG-20240116-008', 'Asignado', 2),
(5, 'PKG-20240116-009', 'Asignado', 3),
-- Ruta 6 (ABC-124, 2024-01-17) - 3 packages
(6, 'PKG-20240116-010', 'Asignado', 1),
(6, 'PKG-20240117-001', 'Asignado', 2),
(6, 'PKG-20240117-002', 'Asignado', 3),
-- Ruta 7 (ABC-126, 2024-01-18) - 3 packages
(7, 'PKG-20240117-003', 'Asignado', 1),
(7, 'PKG-20240117-004', 'Asignado', 2),
(7, 'PKG-20240117-005', 'Asignado', 3),
-- Ruta 8 (ABC-127, 2024-01-18) - 3 packages
(8, 'PKG-20240117-006', 'Asignado', 1),
(8, 'PKG-20240117-007', 'Asignado', 2),
(8, 'PKG-20240117-008', 'Asignado', 3),
-- Ruta 9 (ABC-129, 2024-01-19) - 3 packages
(9, 'PKG-20240117-009', 'Asignado', 1),
(9, 'PKG-20240117-010', 'Asignado', 2),
(9, 'PKG-20240115-001', 'Asignado', 3),
-- Ruta 10 (ABC-130, 2024-01-19) - 2 packages
(10, 'PKG-20240115-002', 'Asignado', 1),
(10, 'PKG-20240115-003', 'Asignado', 2);

-- Update package states to match route assignments
UPDATE `paquete` SET `estado` = 'Asignado_Ruta' WHERE `id_seguimiento` IN (
    'PKG-20240115-001','PKG-20240115-002','PKG-20240115-003','PKG-20240115-004','PKG-20240115-005',
    'PKG-20240115-006','PKG-20240115-007','PKG-20240115-008','PKG-20240115-009','PKG-20240115-010',
    'PKG-20240116-001','PKG-20240116-002','PKG-20240116-003','PKG-20240116-004','PKG-20240116-005',
    'PKG-20240116-006','PKG-20240116-007','PKG-20240116-008','PKG-20240116-009','PKG-20240116-010',
    'PKG-20240117-001','PKG-20240117-002','PKG-20240117-003','PKG-20240117-004','PKG-20240117-005',
    'PKG-20240117-006','PKG-20240117-007','PKG-20240117-008','PKG-20240117-009','PKG-20240117-010'
);

UPDATE `paquete` SET `estado` = 'En_Transito' WHERE `id_seguimiento` IN (
    'PKG-20240115-003','PKG-20240116-004'
);

UPDATE `paquete` SET `estado` = 'Entregado' WHERE `id_seguimiento` IN (
    'PKG-20240115-001','PKG-20240115-002','PKG-20240116-003'
);

-- ============================================================
-- MANTENIMIENTO (25 records)
-- ============================================================
INSERT INTO `mantenimiento` (`vehiculo_placa`, `fecha`, `tipo`, `descripcion`, `costo`, `kilometraje`, `proveedor`) VALUES
('ABC-123', '2023-06-15', 'Preventivo', 'Cambio de aceite y filtros', 450000.00, 45000, 'Chevrolet Servicio Autorizado'),
('ABC-123', '2023-12-10', 'Preventivo', 'Revisión frenos y suspensión', 680000.00, 65000, 'Chevrolet Servicio Autorizado'),
('ABC-124', '2023-07-20', 'Preventivo', 'Cambio de aceite y filtros', 450000.00, 42000, 'Chevrolet Servicio Autorizado'),
('ABC-125', '2023-05-10', 'Correctivo', 'Reparación sistema de frenos ABS', 1250000.00, 78000, 'Ford Service Center'),
('ABC-125', '2023-11-05', 'Preventivo', 'Mantenimiento 80.000 km', 890000.00, 80000, 'Ford Service Center'),
('ABC-126', '2023-08-15', 'Preventivo', 'Cambio de aceite y filtros', 480000.00, 38000, 'Ford Service Center'),
('ABC-127', '2023-04-20', 'Preventivo', 'Mantenimiento 100.000 km', 2100000.00, 100000, 'Volvo Trucks Colombia'),
('ABC-127', '2023-10-10', 'Inspeccion', 'Inspección técnico-mecánica', 180000.00, 115000, 'CDA Autorizado'),
('ABC-128', '2023-06-30', 'Correctivo', 'Reparación transmisión', 3500000.00, 95000, 'Volvo Trucks Colombia'),
('ABC-129', '2023-07-10', 'Preventivo', 'Cambio aceites y filtros', 720000.00, 55000, 'Mercedes-Benz Camiones'),
('ABC-129', '2024-01-05', 'Preventivo', 'Revisión sistema eléctrico', 560000.00, 72000, 'Mercedes-Benz Camiones'),
('ABC-130', '2023-09-25', 'Preventivo', 'Mantenimiento 60.000 km', 850000.00, 60000, 'Mercedes-Benz Camiones'),
('ABC-131', '2023-03-15', 'Preventivo', 'Mantenimiento 120.000 km', 2400000.00, 120000, 'Scania Colombia'),
('ABC-131', '2023-09-10', 'Inspeccion', 'Inspección técnico-mecánica', 180000.00, 135000, 'CDA Autorizado'),
('ABC-132', '2023-05-20', 'Correctivo', 'Cambio embrague', 1800000.00, 88000, 'Scania Colombia'),
('ABC-132', '2023-11-20', 'Preventivo', 'Revisión sistema neumático', 680000.00, 98000, 'Scania Colombia'),
('ABC-133', '2023-08-05', 'Preventivo', 'Cambio de aceite y filtros', 520000.00, 48000, 'International Camiones'),
('ABC-134', '2023-10-20', 'Preventivo', 'Mantenimiento 50.000 km', 780000.00, 50000, 'International Camiones'),
('ABC-135', '2023-02-10', 'Preventivo', 'Mantenimiento 150.000 km', 2800000.00, 150000, 'Kenworth Colombia'),
('ABC-136', '2023-06-15', 'Correctivo', 'Reparación sistema de dirección', 1650000.00, 92000, 'Kenworth Colombia'),
('ABC-137', '2023-07-30', 'Preventivo', 'Cambio aceites y filtros', 850000.00, 65000, 'Peterbilt Colombia'),
('ABC-138', '2023-09-15', 'Preventivo', 'Mantenimiento 80.000 km', 1200000.00, 80000, 'Peterbilt Colombia'),
('ABC-139', '2023-04-25', 'Preventivo', 'Mantenimiento 110.000 km', 2200000.00, 110000, 'Freightliner Colombia'),
('ABC-140', '2023-12-01', 'Correctivo', 'Reparación turbo', 2100000.00, 98000, 'Freightliner Colombia'),
('ABC-141', '2023-05-15', 'Preventivo', 'Cambio de aceite y filtros', 680000.00, 52000, 'MAN Colombia');

-- ============================================================
-- AUDITORIA (sample audit trail)
-- ============================================================
INSERT INTO `auditoria` (`timestamp`, `operacion`, `entidad`, `entidad_id`, `usuario`, `detalles`, `ip_origen`) VALUES
('2024-01-15 08:00:00.000', 'CREATE_RUTA', 'ruta', '1', 'operador1', '{"vehiculo": "ABC-125", "conductor": "1000000003", "fecha": "2024-01-15"}', '192.168.1.100'),
('2024-01-15 08:05:00.000', 'ASSIGN_PACKAGES', 'ruta', '1', 'operador1', '{"package_count": 5, "total_weight_kg": 196.45}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'START_RUTA', 'ruta', '1', 'operador1', '{"old_state": "Planificada", "new_state": "En_Curso"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_VEHICULO', 'vehiculo', 'ABC-125', 'operador1', '{"old": "Disponible", "new": "En_Ruta"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_CONDUCTOR', 'conductor', '1000000003', 'operador1', '{"old": "Activo", "new": "Activo"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_PAQUETE', 'paquete', 'PKG-20240115-001', 'operador1', '{"old": "Asignado_Ruta", "new": "En_Transito"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_PAQUETE', 'paquete', 'PKG-20240115-002', 'operador1', '{"old": "Asignado_Ruta", "new": "En_Transito"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_PAQUETE', 'paquete', 'PKG-20240115-003', 'operador1', '{"old": "Asignado_Ruta", "new": "En_Transito"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_PAQUETE', 'paquete', 'PKG-20240115-004', 'operador1', '{"old": "Asignado_Ruta", "new": "En_Transito"}', '192.168.1.100'),
('2024-01-15 08:30:00.000', 'STATE_CHANGE_PAQUETE', 'paquete', 'PKG-20240115-005', 'operador1', '{"old": "Asignado_Ruta", "new": "En_Transito"}', '192.168.1.100'),
('2024-01-15 10:15:00.000', 'DELIVER_PAQUETE', 'paquete', 'PKG-20240115-001', 'conductor_1000000003', '{"route_id": 1, "orden": 1, "observaciones": "Entregado en recepción"}', '192.168.1.100'),
('2024-01-15 10:45:00.000', 'DELIVER_PAQUETE', 'paquete', 'PKG-20240115-002', 'conductor_1000000003', '{"route_id": 1, "orden": 2, "observaciones": "Entregado a gerente"}', '192.168.1.100'),
('2024-01-15 08:00:00.000', 'CREATE_RUTA', 'ruta', '2', 'operador1', '{"vehiculo": "ABC-132", "conductor": "1000000010", "fecha": "2024-01-15"}', '192.168.1.100'),
('2024-01-15 08:10:00.000', 'ASSIGN_PACKAGES', 'ruta', '2', 'operador1', '{"package_count": 4, "total_weight_kg": 237.50}', '192.168.1.100'),
('2024-01-16 07:30:00.000', 'CREATE_RUTA', 'ruta', '4', 'operador2', '{"vehiculo": "ABC-144", "conductor": "1000000022", "fecha": "2024-01-16"}', '192.168.1.101'),
('2024-01-16 07:35:00.000', 'ASSIGN_PACKAGES', 'ruta', '4', 'operador2', '{"package_count": 4, "total_weight_kg": 255.00}', '192.168.1.101'),
('2024-01-16 08:00:00.000', 'START_RUTA', 'ruta', '4', 'operador2', '{"old_state": "Planificada", "new_state": "En_Curso"}', '192.168.1.101'),
('2024-01-16 08:00:00.000', 'STATE_CHANGE_VEHICULO', 'vehiculo', 'ABC-144', 'operador2', '{"old": "Disponible", "new": "En_Ruta"}', '192.168.1.101'),
('2024-01-16 08:00:00.000', 'STATE_CHANGE_CONDUCTOR', 'conductor', '1000000022', 'operador2', '{"old": "Activo", "new": "Activo"}', '192.168.1.101'),
('2024-01-16 09:30:00.000', 'DELIVER_PAQUETE', 'paquete', 'PKG-20240116-003', 'conductor_1000000022', '{"route_id": 4, "orden": 1, "observaciones": "Entregado en bodega"}', '192.168.1.101');

SET FOREIGN_KEY_CHECKS = 1;