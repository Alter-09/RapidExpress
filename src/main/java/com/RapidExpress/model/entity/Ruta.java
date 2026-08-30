package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.EstadoRuta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ruta {
    private Long idRuta;
    private LocalDate fecha;
    private String vehiculoPlaca;
    private String conductorId;
    private EstadoRuta estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Ruta() {
        this.estado = EstadoRuta.PLANIFICADA;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public Ruta(Long idRuta, LocalDate fecha, String vehiculoPlaca, String conductorId, EstadoRuta estado) {
        this.idRuta = idRuta;
        this.fecha = fecha;
        this.vehiculoPlaca = vehiculoPlaca;
        this.conductorId = conductorId;
        this.estado = estado != null ? estado : EstadoRuta.PLANIFICADA;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Long idRuta) {
        this.idRuta = idRuta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        if (vehiculoPlaca == null || vehiculoPlaca.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa del vehículo no puede ser nula o vacía");
        }
        this.vehiculoPlaca = vehiculoPlaca.toUpperCase().trim();
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        if (conductorId == null || conductorId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del conductor no puede ser nulo o vacío");
        }
        this.conductorId = conductorId.trim();
    }

    public EstadoRuta getEstado() {
        return estado;
    }

    public void setEstado(EstadoRuta estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = estado;
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
    public boolean isPlanificada() {
        return this.estado == EstadoRuta.PLANIFICADA;
    }

    public boolean isEnCurso() {
        return this.estado == EstadoRuta.EN_CURSO;
    }

    public boolean isCompletada() {
        return this.estado == EstadoRuta.COMPLETADA;
    }

    public boolean isCancelada() {
        return this.estado == EstadoRuta.CANCELADA;
    }

    public void iniciarRuta() {
        this.estado = EstadoRuta.EN_CURSO;
        this.updatedAt = LocalDateTime.now();
    }

    public void completarRuta() {
        this.estado = EstadoRuta.COMPLETADA;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelarRuta() {
        this.estado = EstadoRuta.CANCELADA;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ruta ruta = (Ruta) o;
        return Objects.equals(idRuta, ruta.idRuta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRuta);
    }

    @Override
    public String toString() {
        return String.format("Ruta{id=%d, fecha=%s, vehiculo='%s', conductor='%s', estado=%s}", 
                           idRuta, fecha, vehiculoPlaca, conductorId, estado);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long idRuta;
        private LocalDate fecha;
        private String vehiculoPlaca;
        private String conductorId;
        private EstadoRuta estado = EstadoRuta.PLANIFICADA;

        public Builder idRuta(Long idRuta) {
            this.idRuta = idRuta;
            return this;
        }

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder vehiculoPlaca(String vehiculoPlaca) {
            this.vehiculoPlaca = vehiculoPlaca;
            return this;
        }

        public Builder conductorId(String conductorId) {
            this.conductorId = conductorId;
            return this;
        }

        public Builder estado(EstadoRuta estado) {
            this.estado = estado;
            return this;
        }

        public Ruta build() {
            return new Ruta(idRuta, fecha, vehiculoPlaca, conductorId, estado);
        }
    }
}