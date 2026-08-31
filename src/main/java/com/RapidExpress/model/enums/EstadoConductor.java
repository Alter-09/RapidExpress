package com.RapidExpress.model.enums;

public enum EstadoConductor {
    ACTIVO("Activo"),
    DE_VACACIONES("De_Vacaciones"),
    INACTIVO("Inactivo"),
    EN_RUTA("En_Ruta");

    private final String descripcion;

    EstadoConductor(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoConductor fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del conductor no puede ser nulo o vacío");
        }
        
        for (EstadoConductor estado : EstadoConductor.values()) {
            if (estado.descripcion.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de conductor inválido: " + value);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}