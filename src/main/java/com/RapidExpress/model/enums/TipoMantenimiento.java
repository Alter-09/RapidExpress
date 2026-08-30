package com.RapidExpress.model.enums;

public enum TipoMantenimiento {
    PREVENTIVO("Preventivo", "Mantenimiento Preventivo"),
    CORRECTIVO("Correctivo", "Mantenimiento Correctivo"),
    INSPECCION("Inspeccion", "Inspección Técnica");

    private final String tipo;
    private final String descripcion;

    TipoMantenimiento(String tipo, String descripcion) {
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoMantenimiento fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de mantenimiento no puede ser nulo o vacío");
        }

        String normalized = value.trim();
        for (TipoMantenimiento tm : TipoMantenimiento.values()) {
            if (tm.tipo.equalsIgnoreCase(normalized) || tm.name().equalsIgnoreCase(normalized)) {
                return tm;
            }
        }
        throw new IllegalArgumentException("Tipo de mantenimiento inválido: " + value);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", tipo, descripcion);
    }
}