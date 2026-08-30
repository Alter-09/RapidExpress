# RapidExpress - Entity Relationship Diagram (ERD)

## Mermaid Diagram

```mermaid
erDiagram
    VEHICULO ||--o| CONDUCTOR : "assigned_to"
    VEHICULO ||--o{ RUTA : "used_in"
    VEHICULO ||--o{ MANTENIMIENTO : "has"
    CONDUCTOR ||--o{ RUTA : "drives"
    RUTA ||--o{ RUTA_PAQUETE : "contains"
    PAQUETE ||--o{ RUTA_PAQUETE : "assigned_to"
    RUTA ||--o{ AUDITORIA : "audited"
    VEHICULO ||--o{ AUDITORIA : "audited"
    CONDUCTOR ||--o{ AUDITORIA : "audited"
    PAQUETE ||--o{ AUDITORIA : "audited"
    MANTENIMIENTO ||--o{ AUDITORIA : "audited"

    VEHICULO {
        VARCHAR(10) placa PK
        VARCHAR(50) marca
        VARCHAR(50) modelo
        SMALLINT anio_fabricacion
        DECIMAL(10,2) capacidad_max_kg
        ENUM estado
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    CONDUCTOR {
        VARCHAR(20) num_identificacion PK
        VARCHAR(100) nombre_completo
        ENUM tipo_licencia
        VARCHAR(20) num_contacto
        ENUM estado
        VARCHAR(10) vehiculo_asignado FK,UK
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    PAQUETE {
        VARCHAR(36) id_seguimiento PK
        VARCHAR(255) descripcion
        DECIMAL(8,2) peso_kg
        VARCHAR(50) dimensiones
        VARCHAR(255) dir_origen
        VARCHAR(255) dir_destino
        VARCHAR(100) remitente_nombre
        VARCHAR(20) remitente_contacto
        VARCHAR(255) remitente_direccion
        VARCHAR(100) destinatario_nombre
        VARCHAR(20) destinatario_contacto
        VARCHAR(255) destinatario_direccion
        ENUM estado
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    RUTA {
        BIGINT id_ruta PK
        DATE fecha
        VARCHAR(10) vehiculo_placa FK
        VARCHAR(20) conductor_id FK
        ENUM estado
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    RUTA_PAQUETE {
        BIGINT ruta_id PK,FK
        VARCHAR(36) paquete_id PK,FK
        ENUM estado_paquete
        SMALLINT orden_entrega
        TEXT observaciones
        TIMESTAMP actualizado_en
    }

    MANTENIMIENTO {
        BIGINT id_mantenimiento PK
        VARCHAR(10) vehiculo_placa FK
        DATE fecha
        ENUM tipo
        TEXT descripcion
        DECIMAL(12,2) costo
        INT kilometraje
        VARCHAR(100) proveedor
        TIMESTAMP created_at
    }

    AUDITORIA {
        BIGINT id_auditoria PK
        DATETIME(3) timestamp
        VARCHAR(50) operacion
        VARCHAR(50) entidad
        VARCHAR(100) entidad_id
        VARCHAR(100) usuario
        JSON detalles
        VARCHAR(45) ip_origen
    }
```

## Text-Based ERD (Alternative)

```
┌─────────────────┐       ┌──────────────────┐       ┌─────────────┐
│    VEHICULO     │       │    CONDUCTOR     │       │    RUTA     │
├─────────────────┤       ├──────────────────┤       ├─────────────┤
│ PK placa        │◄─────►│ PK num_identif.  │       │ PK id_ruta  │
│ marca           │ 1:1   │ nombre_completo  │       │ fecha       │
│ modelo          │       │ tipo_licencia    │       │ FK vehiculo │
│ anio_fabricacion│       │ num_contacto     │       │ FK conductor│
│ capacidad_max_kg│       │ estado           │       │ estado      │
│ estado          │       │ FK vehiculo_asig.│       │ created_at  │
│ created_at      │       │ created_at       │       │ updated_at  │
│ updated_at      │       │ updated_at       │       └──────┬──────┘
└────────┬────────┘       └──────────────────┘              │
         │                                                  │
         │ 1:N                                              │ 1:N
         ▼                                                  ▼
┌─────────────────┐                            ┌─────────────────────┐
│  MANTENIMIENTO  │                            │   RUTA_PAQUETE      │
├─────────────────┤                            ├─────────────────────┤
│ PK id_mantenim. │                            │ PK ruta_id (FK)     │
│ FK vehiculo     │                            │ PK paquete_id (FK)  │
│ fecha           │                            │ estado_paquete      │
│ tipo            │                            │ orden_entrega       │
│ descripcion     │                            │ observaciones       │
│ costo           │                            │ actualizado_en      │
│ kilometraje     │                            └──────────┬──────────┘
│ proveedor       │                                       │
│ created_at      │                                       │ N:M
└─────────────────┘                                       ▼
                                                          ┌─────────────┐
                                                          │   PAQUETE   │
                                                          ├─────────────┤
                                                          │ PK id_seguim│
                                                          │ descripcion │
                                                          │ peso_kg     │
                                                          │ dimensiones │
                                                          │ dir_origen  │
                                                          │ dir_destino │
                                                          │ remitente_* │
                                                          │ destinatario*│
                                                          │ estado      │
                                                          │ created_at  │
                                                          │ updated_at  │
                                                          └─────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                        AUDITORIA (Centralized Log)                  │
├─────────────────────────────────────────────────────────────────────┤
│ PK id_auditoria  │ timestamp │ operacion │ entidad │ entidad_id    │
│ usuario          │ detalles(JSON)    │ ip_origen                   │
└─────────────────────────────────────────────────────────────────────┘
```

## Relationship Summary

| Relationship | Type | Description |
|-------------|------|-------------|
| `vehiculo` → `conductor` | 1:1 (optional) | One vehicle assigned to at most one driver; one driver drives at most one vehicle |
| `vehiculo` → `ruta` | 1:N | A vehicle can be used in many routes over time |
| `conductor` → `ruta` | 1:N | A driver can drive many routes over time |
| `ruta` → `ruta_paquete` | 1:N | A route contains many package assignments |
| `paquete` → `ruta_paquete` | 1:N | A package can be assigned to multiple routes (e.g., returned & re-shipped) |
| `vehiculo` → `mantenimiento` | 1:N | A vehicle has many maintenance records |
| All entities → `auditoria` | 1:N | Every critical operation on any entity creates an audit entry |

## Normalization Level

- **1NF**: ✅ All attributes atomic, no repeating groups
- **2NF**: ✅ No partial dependencies (all non-key attributes depend on full PK)
- **3NF**: ✅ No transitive dependencies
- **BCNF**: ✅ Every determinant is a candidate key
- **4NF**: ✅ No multi-valued dependencies (resolved via `ruta_paquete` junction)

## Key Design Decisions

1. **Natural PKs** for `vehiculo.placa` and `conductor.num_identificacion` — business identifiers
2. **Surrogate PK** (`BIGINT AUTO_INCREMENT`) for `ruta`, `mantenimiento`, `auditoria` — performance
3. **UUID** (`VARCHAR(36)`) for `paquete.id_seguimiento` — globally unique tracking IDs
4. **Junction table `ruta_paquete`** — resolves many-to-many with per-package route status
5. **ENUM columns** — enforce valid states at DB level (cheaper than CHECK constraints)
6. **Audit table** — append-only, denormalized for write performance; JSON for flexible details
7. **Views** — `v_ruta_detalle` and `v_vehiculo_estado` for common reporting queries