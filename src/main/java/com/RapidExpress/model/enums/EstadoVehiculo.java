package com.RapidExpress.model.enums;

public enum EstadoVehiculo {
    DISPONIBLE("Disponible"),
    EN_RUTA("En_Ruta"),
    EN_MANTENIMIENTO("En_Mantenimiento");

    private final String descripcion;

    EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoVehiculo fromString(String value) {
        for (EstadoVehiculo estado : EstadoVehiculo.values()) {
            if (estado.descripcion.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de vehículo inválido: " + value);
    }
}