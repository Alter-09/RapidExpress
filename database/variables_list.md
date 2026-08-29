# RapidExpress - Data Dictionary (Variables List)

## Overview
Complete data dictionary for the RapidExpress Fleet Management System database. All tables use `utf8mb4_unicode_ci` collation, InnoDB engine.

---

## Table: `vehiculo` (Fleet Vehicles)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `placa` | VARCHAR(10) | PK, NOT NULL | Unique license plate (primary key) |
| `marca` | VARCHAR(50) | NOT NULL | Vehicle brand (e.g., Chevrolet, Ford, Volvo) |
| `modelo` | VARCHAR(50) | NOT NULL | Vehicle model (e.g., NPR 700, FH 540) |
| `anio_fabricacion` | SMALLINT UNSIGNED | NOT NULL, CHECK (1990-2030) | Manufacturing year |
| `capacidad_max_kg` | DECIMAL(10,2) UNSIGNED | NOT NULL, CHECK (>0) | Maximum load capacity in kilograms |
| `estado` | ENUM | NOT NULL, DEFAULT 'Disponible' | Current status: `Disponible`, `En_Ruta`, `En_Mantenimiento` |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

**Indexes:** PRIMARY KEY (`placa`)

---

## Table: `conductor` (Drivers)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `num_identificacion` | VARCHAR(20) | PK, NOT NULL | National ID number (primary key) |
| `nombre_completo` | VARCHAR(100) | NOT NULL | Full legal name |
| `tipo_licencia` | ENUM | NOT NULL | License class: `A`, `B`, `C`, `D`, `E` |
| `num_contacto` | VARCHAR(20) | NOT NULL | Phone number |
| `estado` | ENUM | NOT NULL, DEFAULT 'Activo' | Status: `Activo`, `De_Vacaciones`, `Inactivo` |
| `vehiculo_asignado` | VARCHAR(10) | FK, UNIQUE, NULLABLE | Assigned vehicle plate (1:1 relationship) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

**Indexes:** PRIMARY KEY (`num_identificacion`), UNIQUE KEY `uk_conductor_vehiculo` (`vehiculo_asignado`)

**Foreign Keys:** `fk_conductor_vehiculo` → `vehiculo.placa` (ON DELETE SET NULL, ON UPDATE CASCADE)

---

## Table: `paquete` (Packages/Shipments)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id_seguimiento` | VARCHAR(36) | PK, NOT NULL | UUID tracking ID (primary key) |
| `descripcion` | VARCHAR(255) | NOT NULL | Content description |
| `peso_kg` | DECIMAL(8,2) UNSIGNED | NOT NULL, CHECK (>0) | Weight in kilograms |
| `dimensiones` | VARCHAR(50) | NOT NULL | Dimensions as "LxWxH cm" (e.g., "30x20x15") |
| `dir_origen` | VARCHAR(255) | NOT NULL | Origin address |
| `dir_destino` | VARCHAR(255) | NOT NULL | Destination address |
| `remitente_nombre` | VARCHAR(100) | NOT NULL | Sender full name |
| `remitente_contacto` | VARCHAR(20) | NOT NULL | Sender phone |
| `remitente_direccion` | VARCHAR(255) | NOT NULL | Sender address |
| `destinatario_nombre` | VARCHAR(100) | NOT NULL | Recipient full name |
| `destinatario_contacto` | VARCHAR(20) | NOT NULL | Recipient phone |
| `destinatario_direccion` | VARCHAR(255) | NOT NULL | Recipient address |
| `estado` | ENUM | NOT NULL, DEFAULT 'En_Bodega' | Status: `En_Bodega`, `Asignado_Ruta`, `En_Transito`, `Entregado`, `Devuelto` |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

**Indexes:** PRIMARY KEY (`id_seguimiento`)

---

## Table: `ruta` (Routes)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id_ruta` | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Route ID (primary key) |
| `fecha` | DATE | NOT NULL | Route date |
| `vehiculo_placa` | VARCHAR(10) | FK, NOT NULL | Assigned vehicle |
| `conductor_id` | VARCHAR(20) | FK, NOT NULL | Assigned driver |
| `estado` | ENUM | NOT NULL, DEFAULT 'Planificada' | Status: `Planificada`, `En_Curso`, `Completada`, `Cancelada` |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

**Indexes:** PRIMARY KEY (`id_ruta`), KEY `idx_ruta_fecha` (`fecha`), KEY `idx_ruta_vehiculo` (`vehiculo_placa`), KEY `idx_ruta_conductor` (`conductor_id`)

**Foreign Keys:**
- `fk_ruta_vehiculo` → `vehiculo.placa` (ON DELETE RESTRICT, ON UPDATE CASCADE)
- `fk_ruta_conductor` → `conductor.num_identificacion` (ON DELETE RESTRICT, ON UPDATE CASCADE)

---

## Table: `ruta_paquete` (Route-Package Junction + Per-Package Status)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `ruta_id` | BIGINT UNSIGNED | PK, FK, NOT NULL | Route reference |
| `paquete_id` | VARCHAR(36) | PK, FK, NOT NULL | Package reference |
| `estado_paquete` | ENUM | NOT NULL, DEFAULT 'Asignado' | Package status on this route: `Asignado`, `En_Transito`, `Entregado`, `Devuelto`, `No_Entregado` |
| `orden_entrega` | SMALLINT UNSIGNED | NOT NULL, DEFAULT 0 | Delivery sequence order |
| `observaciones` | TEXT | NULLABLE | Delivery notes/exceptions |
| `actualizado_en` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last status change timestamp |

**Indexes:** PRIMARY KEY (`ruta_id`, `paquete_id`), KEY `idx_ruta_paquete_paquete` (`paquete_id`)

**Foreign Keys:**
- `fk_ruta_paquete_ruta` → `ruta.id_ruta` (ON DELETE CASCADE, ON UPDATE CASCADE)
- `fk_ruta_paquete_paquete` → `paquete.id_seguimiento` (ON DELETE RESTRICT, ON UPDATE CASCADE)

---

## Table: `mantenimiento` (Vehicle Maintenance Records)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id_mantenimiento` | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Maintenance ID (primary key) |
| `vehiculo_placa` | VARCHAR(10) | FK, NOT NULL | Vehicle reference |
| `fecha` | DATE | NOT NULL | Maintenance date |
| `tipo` | ENUM | NOT NULL | Type: `Preventivo`, `Correctivo`, `Inspeccion` |
| `descripcion` | TEXT | NOT NULL | Work description |
| `costo` | DECIMAL(12,2) UNSIGNED | NOT NULL, DEFAULT 0, CHECK (>=0) | Cost in local currency |
| `kilometraje` | INT UNSIGNED | NULLABLE | Odometer reading at time of service |
| `proveedor` | VARCHAR(100) | NULLABLE | Service provider name |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |

**Indexes:** PRIMARY KEY (`id_mantenimiento`), KEY `idx_mantenimiento_vehiculo` (`vehiculo_placa`), KEY `idx_mantenimiento_fecha` (`fecha`)

**Foreign Keys:** `fk_mantenimiento_vehiculo` → `vehiculo.placa` (ON DELETE CASCADE, ON UPDATE CASCADE)

---

## Table: `auditoria` (Centralized Audit Log)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id_auditoria` | BIGINT UNSIGNED | PK, AUTO_INCREMENT | Audit entry ID (primary key) |
| `timestamp` | DATETIME(3) | NOT NULL, DEFAULT CURRENT_TIMESTAMP(3) | Millisecond-precision timestamp |
| `operacion` | VARCHAR(50) | NOT NULL | Operation type: `CREATE`, `UPDATE`, `DELETE`, `STATE_CHANGE`, `ASSIGN`, `UNASSIGN`, `START_ROUTE`, `END_ROUTE`, `DELIVER_PACKAGE`, `RETURN_PACKAGE`, etc. |
| `entidad` | VARCHAR(50) | NOT NULL | Entity name: `vehiculo`, `conductor`, `paquete`, `ruta`, `mantenimiento` |
| `entidad_id` | VARCHAR(100) | NOT NULL | Primary key value of affected entity |
| `usuario` | VARCHAR(100) | NULLABLE | Operator username/ID who performed action |
| `detalles` | JSON | NULLABLE | Structured data: old/new values, context (e.g., `{"old": "Disponible", "new": "En_Ruta", "route_id": 123}`) |
| `ip_origen` | VARCHAR(45) | NULLABLE | Client IP (IPv4 or IPv6) |

**Indexes:** PRIMARY KEY (`id_auditoria`), KEY `idx_auditoria_timestamp` (`timestamp`), KEY `idx_auditoria_entidad` (`entidad`, `entidad_id`), KEY `idx_auditoria_operacion` (`operacion`)

---

## Views

### `v_ruta_detalle` — Route Detail for Reporting
Aggregated route information with package counts and weight totals.

| Column | Source | Description |
|--------|--------|-------------|
| `id_ruta` | ruta | Route ID |
| `fecha` | ruta | Route date |
| `ruta_estado` | ruta | Route status |
| `vehiculo_placa` | vehiculo | Vehicle plate |
| `vehiculo_marca` | vehiculo | Vehicle brand |
| `vehiculo_modelo` | vehiculo | Vehicle model |
| `capacidad_max_kg` | vehiculo | Max capacity |
| `conductor_id` | conductor | Driver ID |
| `conductor_nombre` | conductor | Driver name |
| `tipo_licencia` | conductor | License class |
| `total_paquetes` | COUNT(ruta_paquete) | Total packages assigned |
| `paquetes_entregados` | SUM(CASE...) | Delivered count |
| `paquetes_devueltos` | SUM(CASE...) | Returned count |
| `paquetes_no_entregados` | SUM(CASE...) | Not delivered count |
| `peso_total_kg` | SUM(paquete.peso_kg) | Total weight |

### `v_vehiculo_estado` — Vehicle Status Dashboard
Current vehicle state with assigned driver and activity metrics.

| Column | Source | Description |
|--------|--------|-------------|
| `placa` | vehiculo | Vehicle plate |
| `marca` | vehiculo | Brand |
| `modelo` | vehiculo | Model |
| `anio_fabricacion` | vehiculo | Year |
| `capacidad_max_kg` | vehiculo | Capacity |
| `estado` | vehiculo | Current status |
| `conductor_id` | conductor | Assigned driver ID |
| `conductor_nombre` | conductor | Driver name |
| `conductor_estado` | conductor | Driver status |
| `rutas_activas` | subquery | Active routes count |
| `mantenimientos_6m` | subquery | Maintenance count (last 6 months) |

---

## ENUM Value Reference

### `vehiculo.estado`
- `Disponible` — Ready for assignment
- `En_Ruta` — Currently on a route
- `En_Mantenimiento` — In maintenance, unavailable

### `conductor.estado`
- `Activo` — Available for assignment
- `De_Vacaciones` — On leave
- `Inactivo` — Inactive/terminated

### `conductor.tipo_licencia`
- `A` — Motorcycle
- `B` — Car/Light vehicle
- `C` — Truck (rigid)
- `D` — Bus
- `E` — Articulated truck

### `paquete.estado`
- `En_Bodega` — In warehouse, not assigned
- `Asignado_Ruta` — Assigned to a route, not yet in transit
- `En_Transito` — On route, in transit
- `Entregado` — Successfully delivered
- `Devuelto` — Returned to sender/warehouse

### `ruta.estado`
- `Planificada` — Created, not started
- `En_Curso` — Started, in progress
- `Completada` — All packages delivered/returned
- `Cancelada` — Cancelled before start

### `ruta_paquete.estado_paquete`
- `Asignado` — Assigned to route
- `En_Transito` — In transit on route
- `Entregado` — Delivered on this route
- `Devuelto` — Returned on this route
- `No_Entregado` — Attempted but not delivered

### `mantenimiento.tipo`
- `Preventivo` — Scheduled preventive maintenance
- `Correctivo` — Repair after failure
- `Inspeccion` — Regulatory/safety inspection

### `auditoria.operacion` (examples)
- `CREATE_VEHICULO`, `UPDATE_VEHICULO`, `DELETE_VEHICULO`
- `STATE_CHANGE_VEHICULO`, `ASSIGN_CONDUCTOR`, `UNASSIGN_CONDUCTOR`
- `CREATE_PAQUETE`, `UPDATE_PAQUETE`, `STATE_CHANGE_PAQUETE`
- `START_RUTA`, `END_RUTA`, `CANCEL_RUTA`
- `DELIVER_PAQUETE`, `RETURN_PAQUETE`
- `CREATE_MANTENIMIENTO`

---

## Normalization Notes

- **1NF:** All attributes atomic, no repeating groups
- **2NF:** All non-key attributes fully dependent on PK (no partial dependencies)
- **3NF:** No transitive dependencies; all non-key attributes depend only on PK
- **BCNF:** Every determinant is a candidate key
- **Junction table `ruta_paquete`** resolves many-to-many between `ruta` and `paquete` while storing per-package route status
- **Audit table** is append-only, denormalized for write performance and query flexibility