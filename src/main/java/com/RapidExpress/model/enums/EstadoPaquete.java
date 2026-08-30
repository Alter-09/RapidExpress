package com.RapidExpress.model.enums;

public enum EstadoPaquete {
    EN_BODEGA("En_Bodega"),
    ASIGNADO_RUTA("Asignado_Ruta"),
    EN_TRANSITO("En_Transito"),
    ENTREGADO("Entregado"),
    DEVUELTO("Devuelto");

    private final String descripcion;

    EstadoPaquete(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoPaquete fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del paquete no puede ser nulo o vacío");
        }
        
        for (EstadoPaquete estado : EstadoPaquete.values()) {
            if (estado.descripcion.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado de paquete inválido: " + value);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}