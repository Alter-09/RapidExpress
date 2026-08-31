package com.RapidExpress.model.enums;

public enum TipoLicencia {
    A("A", "Motocicleta"),
    B("B", "Automóvil/Vehículo ligero"),
    C("C", "Camión rígido"),
    D("D", "Autobús"),
    E("E", "Camión articulado");

    private final String codigo;
    private final String descripcion;

    TipoLicencia(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoLicencia fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de licencia no puede ser nulo o vacío");
        }
        
        for (TipoLicencia tipo : TipoLicencia.values()) {
            if (tipo.codigo.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de licencia inválido: " + value);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", codigo, descripcion);
    }
}