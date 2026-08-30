package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.EstadoVehiculo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnioFabricacion() { return anioFabricacion; }
    public void setAnioFabricacion(int anioFabricacion) { this.anioFabricacion = anioFabricacion; }

    public BigDecimal getCapacidadMaxKg() { return capacidadMaxKg; }
    public void setCapacidadMaxKg(BigDecimal capacidadMaxKg) { this.capacidadMaxKg = capacidadMaxKg; }

    public EstadoVehiculo getEstado() { return estado; }
    public void setEstado(EstadoVehiculo estado) { 
        this.estado = estado;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format("Vehiculo{placa='%s', marca='%s', modelo='%s', estado=%s}", 
                           placa, marca, modelo, estado);
    }

    public boolean isDisponible() {
        return this.estado == EstadoVehiculo.DISPONIBLE;
    }
}