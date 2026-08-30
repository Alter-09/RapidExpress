package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.EstadoRutaPaquete;

import java.time.LocalDateTime;
import java.util.Objects;

public class RutaPaquete {
    private Long rutaId;
    private String paqueteId;
    private EstadoRutaPaquete estadoPaquete;
    private short ordenEntrega;
    private String observaciones;
    private LocalDateTime actualizadoEn;

    // Constructor vacío
    public RutaPaquete() {
        this.estadoPaquete = EstadoRutaPaquete.ASIGNADO;
        this.ordenEntrega = 0;
        this.actualizadoEn = LocalDateTime.now();
    }

    // Constructor completo
    public RutaPaquete(Long rutaId, String paqueteId, EstadoRutaPaquete estadoPaquete,
                       short ordenEntrega, String observaciones) {
        this.rutaId = rutaId;
        this.paqueteId = paqueteId;
        this.estadoPaquete = estadoPaquete != null ? estadoPaquete : EstadoRutaPaquete.ASIGNADO;
        this.ordenEntrega = ordenEntrega;
        this.observaciones = observaciones;
        this.actualizadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getRutaId() {
        return rutaId;
    }

    public void setRutaId(Long rutaId) {
        if (rutaId == null || rutaId <= 0) {
            throw new IllegalArgumentException("El ID de ruta debe ser mayor a 0");
        }
        this.rutaId = rutaId;
    }

    public String getPaqueteId() {
        return paqueteId;
    }

    public void setPaqueteId(String paqueteId) {
        if (paqueteId == null || paqueteId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del paquete no puede ser nulo o vacío");
        }
        this.paqueteId = paqueteId.trim();
    }

    public EstadoRutaPaquete getEstadoPaquete() {
        return estadoPaquete;
    }

    public void setEstadoPaquete(EstadoRutaPaquete estadoPaquete) {
        if (estadoPaquete == null) {
            throw new IllegalArgumentException("El estado del paquete no puede ser nulo");
        }
        this.estadoPaquete = estadoPaquete;
        this.actualizadoEn = LocalDateTime.now();
    }

    public short getOrdenEntrega() {
        return ordenEntrega;
    }

    public void setOrdenEntrega(short ordenEntrega) {
        if (ordenEntrega < 0) {
            throw new IllegalArgumentException("El orden de entrega no puede ser negativo");
        }
        this.ordenEntrega = ordenEntrega;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
        this.actualizadoEn = LocalDateTime.now();
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    // Métodos de utilidad
    public boolean isAsignado() {
        return this.estadoPaquete == EstadoRutaPaquete.ASIGNADO;
    }

    public boolean isEnTransito() {
        return this.estadoPaquete == EstadoRutaPaquete.EN_TRANSITO;
    }

    public boolean isEntregado() {
        return this.estadoPaquete == EstadoRutaPaquete.ENTREGADO;
    }

    public boolean isDevuelto() {
        return this.estadoPaquete == EstadoRutaPaquete.DEVUELTO;
    }

    public boolean isNoEntregado() {
        return this.estadoPaquete == EstadoRutaPaquete.NO_ENTREGADO;
    }

    public void marcarEntregado(String observacion) {
        this.estadoPaquete = EstadoRutaPaquete.ENTREGADO;
        this.observaciones = observacion;
        this.actualizadoEn = LocalDateTime.now();
    }

    public void marcarDevuelto(String observacion) {
        this.estadoPaquete = EstadoRutaPaquete.DEVUELTO;
        this.observaciones = observacion;
        this.actualizadoEn = LocalDateTime.now();
    }

    public void marcarNoEntregado(String observacion) {
        this.estadoPaquete = EstadoRutaPaquete.NO_ENTREGADO;
        this.observaciones = observacion;
        this.actualizadoEn = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RutaPaquete that = (RutaPaquete) o;
        return Objects.equals(rutaId, that.rutaId) && 
               Objects.equals(paqueteId, that.paqueteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rutaId, paqueteId);
    }

    @Override
    public String toString() {
        return String.format("RutaPaquete{rutaId=%d, paqueteId='%s', estado=%s, orden=%d}", 
                           rutaId, paqueteId, estadoPaquete, ordenEntrega);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long rutaId;
        private String paqueteId;
        private EstadoRutaPaquete estadoPaquete = EstadoRutaPaquete.ASIGNADO;
        private short ordenEntrega = 0;
        private String observaciones;

        public Builder rutaId(Long rutaId) {
            this.rutaId = rutaId;
            return this;
        }

        public Builder paqueteId(String paqueteId) {
            this.paqueteId = paqueteId;
            return this;
        }

        public Builder estadoPaquete(EstadoRutaPaquete estadoPaquete) {
            this.estadoPaquete = estadoPaquete;
            return this;
        }

        public Builder ordenEntrega(short ordenEntrega) {
            this.ordenEntrega = ordenEntrega;
            return this;
        }

        public Builder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public RutaPaquete build() {
            return new RutaPaquete(rutaId, paqueteId, estadoPaquete, ordenEntrega, observaciones);
        }
    }
}