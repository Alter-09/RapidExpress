package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.TipoLicencia;

import java.time.LocalDateTime;
import java.util.Objects;

public class Conductor {
    private String numIdentificacion;
    private String nombreCompleto;
    private TipoLicencia tipoLicencia;
    private String numContacto;
    private EstadoConductor estado;
    private String vehiculoAsignado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Conductor() {
        this.estado = EstadoConductor.ACTIVO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public Conductor(String numIdentificacion, String nombreCompleto, TipoLicencia tipoLicencia,
                     String numContacto, EstadoConductor estado, String vehiculoAsignado) {
        this.numIdentificacion = numIdentificacion;
        this.nombreCompleto = nombreCompleto;
        this.tipoLicencia = tipoLicencia;
        this.numContacto = numContacto;
        this.estado = estado != null ? estado : EstadoConductor.ACTIVO;
        this.vehiculoAsignado = vehiculoAsignado;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getNumIdentificacion() {
        return numIdentificacion;
    }

    public void setNumIdentificacion(String numIdentificacion) {
        if (numIdentificacion == null || numIdentificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de identificación no puede ser nulo o vacío");
        }
        this.numIdentificacion = numIdentificacion.trim();
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede ser nulo o vacío");
        }
        this.nombreCompleto = nombreCompleto.trim();
    }

    public TipoLicencia getTipoLicencia() {
        return tipoLicencia;
    }

    public void setTipoLicencia(TipoLicencia tipoLicencia) {
        if (tipoLicencia == null) {
            throw new IllegalArgumentException("El tipo de licencia no puede ser nulo");
        }
        this.tipoLicencia = tipoLicencia;
    }

    public String getNumContacto() {
        return numContacto;
    }

    public void setNumContacto(String numContacto) {
        if (numContacto == null || numContacto.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de contacto no puede ser nulo o vacío");
        }
        this.numContacto = numContacto.trim();
    }

    public EstadoConductor getEstado() {
        return estado;
    }

    public void setEstado(EstadoConductor estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = estado;
        this.updatedAt = LocalDateTime.now();
    }

    public String getVehiculoAsignado() {
        return vehiculoAsignado;
    }

    public void setVehiculoAsignado(String vehiculoAsignado) {
        this.vehiculoAsignado = vehiculoAsignado != null ? vehiculoAsignado.toUpperCase().trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos de utilidad
    public boolean isActivo() {
        return this.estado == EstadoConductor.ACTIVO;
    }

    public boolean isDeVacaciones() {
        return this.estado == EstadoConductor.DE_VACACIONES;
    }

    public boolean isInactivo() {
        return this.estado == EstadoConductor.INACTIVO;
    }

    public boolean isEnRuta() {
        return this.estado == EstadoConductor.EN_RUTA;
    }

    public boolean tieneVehiculoAsignado() {
        return this.vehiculoAsignado != null && !this.vehiculoAsignado.trim().isEmpty();
    }

    public void liberarVehiculo() {
        this.vehiculoAsignado = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void asignarVehiculo(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa del vehículo no puede ser nula o vacía");
        }
        this.vehiculoAsignado = placa.toUpperCase().trim();
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conductor conductor = (Conductor) o;
        return Objects.equals(numIdentificacion, conductor.numIdentificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numIdentificacion);
    }

    @Override
    public String toString() {
        return String.format("Conductor{id='%s', nombre='%s', licencia=%s, contacto='%s', " +
                           "estado=%s, vehiculo='%s'}", 
                           numIdentificacion, nombreCompleto, tipoLicencia, 
                           numContacto, estado, vehiculoAsignado);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String numIdentificacion;
        private String nombreCompleto;
        private TipoLicencia tipoLicencia;
        private String numContacto;
        private EstadoConductor estado = EstadoConductor.ACTIVO;
        private String vehiculoAsignado;

        public Builder numIdentificacion(String numIdentificacion) {
            this.numIdentificacion = numIdentificacion;
            return this;
        }

        public Builder nombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public Builder tipoLicencia(TipoLicencia tipoLicencia) {
            this.tipoLicencia = tipoLicencia;
            return this;
        }

        public Builder numContacto(String numContacto) {
            this.numContacto = numContacto;
            return this;
        }

        public Builder estado(EstadoConductor estado) {
            this.estado = estado;
            return this;
        }

        public Builder vehiculoAsignado(String vehiculoAsignado) {
            this.vehiculoAsignado = vehiculoAsignado;
            return this;
        }

        public Conductor build() {
            return new Conductor(numIdentificacion, nombreCompleto, tipoLicencia, 
                               numContacto, estado, vehiculoAsignado);
        }
    }
}