package com.RapidExpress.model.enums;

public enum EstadoRuta {
    PLANIFICADA("Planificada"),
    EN_CURSO("En_Curso"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoRuta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoRuta fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de la ruta no puede ser nulo o vacío");
        }
        
        for (EstadoRuta estado : EstadoRuta.values()) {
            if (estado.descripcion.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de ruta inválido: " + value);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}