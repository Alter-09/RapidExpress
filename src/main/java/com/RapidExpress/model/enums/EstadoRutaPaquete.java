package com.RapidExpress.model.enums;

public enum EstadoRutaPaquete {
    ASIGNADO("Asignado"),
    EN_TRANSITO("En_Transito"),
    ENTREGADO("Entregado"),
    DEVUELTO("Devuelto"),
    NO_ENTREGADO("No_Entregado");

    private final String descripcion;

    EstadoRutaPaquete(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoRutaPaquete fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del paquete en ruta no puede ser nulo o vacío");
        }
        
        for (EstadoRutaPaquete estado : EstadoRutaPaquete.values()) {
            if (estado.descripcion.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de paquete en ruta inválido: " + value);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}