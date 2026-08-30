package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Vehiculo {
    private String placa;
    private String marca;
    private String modelo;
    private int anioFabricacion;
    private BigDecimal capacidadMaxKg;
    private EstadoVehiculo estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Vehiculo() {
        this.estado = EstadoVehiculo.DISPONIBLE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public Vehiculo(String placa, String marca, String modelo, int anioFabricacion,
                    BigDecimal capacidadMaxKg, EstadoVehiculo estado) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.capacidadMaxKg = capacidadMaxKg;
        this.estado = estado != null ? estado : EstadoVehiculo.DISPONIBLE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede ser nula o vacía");
        }
        this.placa = placa.toUpperCase().trim();
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca no puede ser nula o vacía");
        }
        this.marca = marca.trim();
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo no puede ser nulo o vacío");
        }
        this.modelo = modelo.trim();
    }

    public int getAnioFabricacion() {
        return anioFabricacion;
    }

    public void setAnioFabricacion(int anioFabricacion) {
        if (anioFabricacion < 1990 || anioFabricacion > 2030) {
            throw new IllegalArgumentException("El año de fabricación debe estar entre 1990 y 2030");
        }
        this.anioFabricacion = anioFabricacion;
    }

    public BigDecimal getCapacidadMaxKg() {
        return capacidadMaxKg;
    }

    public void setCapacidadMaxKg(BigDecimal capacidadMaxKg) {
        if (capacidadMaxKg == null || capacidadMaxKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La capacidad máxima debe ser mayor a 0");
        }
        this.capacidadMaxKg = capacidadMaxKg;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
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
    public boolean isDisponible() {
        return this.estado == EstadoVehiculo.DISPONIBLE;
    }

    public boolean isEnRuta() {
        return this.estado == EstadoVehiculo.EN_RUTA;
    }

    public boolean isEnMantenimiento() {
        return this.estado == EstadoVehiculo.EN_MANTENIMIENTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return Objects.equals(placa, vehiculo.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }

    @Override
    public String toString() {
        return String.format("Vehiculo{placa='%s', marca='%s', modelo='%s', año=%d, " +
                           "capacidad=%.2f kg, estado=%s}", 
                           placa, marca, modelo, anioFabricacion, 
                           capacidadMaxKg, estado);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String placa;
        private String marca;
        private String modelo;
        private int anioFabricacion;
        private BigDecimal capacidadMaxKg;
        private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

        public Builder placa(String placa) {
            this.placa = placa;
            return this;
        }

        public Builder marca(String marca) {
            this.marca = marca;
            return this;
        }

        public Builder modelo(String modelo) {
            this.modelo = modelo;
            return this;
        }

        public Builder anioFabricacion(int anioFabricacion) {
            this.anioFabricacion = anioFabricacion;
            return this;
        }

        public Builder capacidadMaxKg(BigDecimal capacidadMaxKg) {
            this.capacidadMaxKg = capacidadMaxKg;
            return this;
        }

        public Builder estado(EstadoVehiculo estado) {
            this.estado = estado;
            return this;
        }

        public Vehiculo build() {
            return new Vehiculo(placa, marca, modelo, anioFabricacion, capacidadMaxKg, estado);
        }
    }
}