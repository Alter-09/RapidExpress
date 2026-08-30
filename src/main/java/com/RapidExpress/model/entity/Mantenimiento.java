package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.TipoMantenimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Mantenimiento {
    private Long idMantenimiento;
    private String vehiculoPlaca;
    private LocalDate fecha;
    private TipoMantenimiento tipo;
    private String descripcion;
    private BigDecimal costo;
    private Integer kilometraje;
    private String proveedor;
    private LocalDateTime createdAt;

    // Constructor vacío
    public Mantenimiento() {
        this.costo = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor completo
    public Mantenimiento(Long idMantenimiento, String vehiculoPlaca, LocalDate fecha,
                         TipoMantenimiento tipo, String descripcion, BigDecimal costo,
                         Integer kilometraje, String proveedor) {
        this.idMantenimiento = idMantenimiento;
        this.vehiculoPlaca = vehiculoPlaca;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.costo = costo != null ? costo : BigDecimal.ZERO;
        this.kilometraje = kilometraje;
        this.proveedor = proveedor;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(Long idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }

    public TipoMantenimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMantenimiento tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de mantenimiento no puede ser nulo");
        }
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        this.descripcion = descripcion.trim();
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        if (costo == null || costo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        this.costo = costo;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Integer kilometraje) {
        if (kilometraje != null && kilometraje < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo");
        }
        this.kilometraje = kilometraje;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor != null ? proveedor.trim() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Métodos de utilidad
    public boolean isPreventivo() {
        return this.tipo == TipoMantenimiento.PREVENTIVO;
    }

    public boolean isCorrectivo() {
        return this.tipo == TipoMantenimiento.CORRECTIVO;
    }

    public boolean isInspeccion() {
        return this.tipo == TipoMantenimiento.INSPECCION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mantenimiento that = (Mantenimiento) o;
        return Objects.equals(idMantenimiento, that.idMantenimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMantenimiento);
    }

    @Override
    public String toString() {
        return String.format("Mantenimiento{id=%d, vehiculo='%s', fecha=%s, tipo=%s, " +
                           "costo=%.2f, proveedor='%s'}", 
                           idMantenimiento, vehiculoPlaca, fecha, tipo, costo, proveedor);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long idMantenimiento;
        private String vehiculoPlaca;
        private LocalDate fecha;
        private TipoMantenimiento tipo;
        private String descripcion;
        private BigDecimal costo = BigDecimal.ZERO;
        private Integer kilometraje;
        private String proveedor;

        public Builder idMantenimiento(Long idMantenimiento) {
            this.idMantenimiento = idMantenimiento;
            return this;
        }

        public Builder vehiculoPlaca(String vehiculoPlaca) {
            this.vehiculoPlaca = vehiculoPlaca;
            return this;
        }

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder tipo(TipoMantenimiento tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder costo(BigDecimal costo) {
            this.costo = costo;
            return this;
        }

        public Builder kilometraje(Integer kilometraje) {
            this.kilometraje = kilometraje;
            return this;
        }

        public Builder proveedor(String proveedor) {
            this.proveedor = proveedor;
            return this;
        }

        public Mantenimiento build() {
            return new Mantenimiento(idMantenimiento, vehiculoPlaca, fecha, tipo, 
                                   descripcion, costo, kilometraje, proveedor);
        }
    }
}