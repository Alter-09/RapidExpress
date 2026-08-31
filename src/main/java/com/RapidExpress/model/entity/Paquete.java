package com.RapidExpress.model.entity;

import com.RapidExpress.model.enums.EstadoPaquete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Paquete {
    private String idSeguimiento;
    private String descripcion;
    private BigDecimal pesoKg;
    private String dimensiones;
    private String dirOrigen;
    private String dirDestino;
    private String remitenteNombre;
    private String remitenteContacto;
    private String remitenteDireccion;
    private String destinatarioNombre;
    private String destinatarioContacto;
    private String destinatarioDireccion;
    private EstadoPaquete estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Paquete() {
        this.idSeguimiento = UUID.randomUUID().toString();
        this.estado = EstadoPaquete.EN_BODEGA;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor completo
    public Paquete(String idSeguimiento, String descripcion, BigDecimal pesoKg, String dimensiones,
                   String dirOrigen, String dirDestino, String remitenteNombre, String remitenteContacto,
                   String remitenteDireccion, String destinatarioNombre, String destinatarioContacto,
                   String destinatarioDireccion, EstadoPaquete estado) {
        this.idSeguimiento = idSeguimiento != null ? idSeguimiento : UUID.randomUUID().toString();
        this.descripcion = descripcion;
        this.pesoKg = pesoKg;
        this.dimensiones = dimensiones;
        this.dirOrigen = dirOrigen;
        this.dirDestino = dirDestino;
        this.remitenteNombre = remitenteNombre;
        this.remitenteContacto = remitenteContacto;
        this.remitenteDireccion = remitenteDireccion;
        this.destinatarioNombre = destinatarioNombre;
        this.destinatarioContacto = destinatarioContacto;
        this.destinatarioDireccion = destinatarioDireccion;
        this.estado = estado != null ? estado : EstadoPaquete.EN_BODEGA;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getIdSeguimiento() {
        return idSeguimiento;
    }

    public void setIdSeguimiento(String idSeguimiento) {
        if (idSeguimiento == null || idSeguimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de seguimiento no puede ser nulo o vacío");
        }
        this.idSeguimiento = idSeguimiento.trim();
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

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        if (pesoKg == null || pesoKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0");
        }
        this.pesoKg = pesoKg;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        if (dimensiones == null || dimensiones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las dimensiones no pueden ser nulas o vacías");
        }
        this.dimensiones = dimensiones.trim();
    }

    public String getDirOrigen() {
        return dirOrigen;
    }

    public void setDirOrigen(String dirOrigen) {
        if (dirOrigen == null || dirOrigen.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección de origen no puede ser nula o vacía");
        }
        this.dirOrigen = dirOrigen.trim();
    }

    public String getDirDestino() {
        return dirDestino;
    }

    public void setDirDestino(String dirDestino) {
        if (dirDestino == null || dirDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección de destino no puede ser nula o vacía");
        }
        this.dirDestino = dirDestino.trim();
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        if (remitenteNombre == null || remitenteNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del remitente no puede ser nulo o vacío");
        }
        this.remitenteNombre = remitenteNombre.trim();
    }

    public String getRemitenteContacto() {
        return remitenteContacto;
    }

    public void setRemitenteContacto(String remitenteContacto) {
        if (remitenteContacto == null || remitenteContacto.trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto del remitente no puede ser nulo o vacío");
        }
        this.remitenteContacto = remitenteContacto.trim();
    }

    public String getRemitenteDireccion() {
        return remitenteDireccion;
    }

    public void setRemitenteDireccion(String remitenteDireccion) {
        if (remitenteDireccion == null || remitenteDireccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección del remitente no puede ser nula o vacía");
        }
        this.remitenteDireccion = remitenteDireccion.trim();
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public void setDestinatarioNombre(String destinatarioNombre) {
        if (destinatarioNombre == null || destinatarioNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del destinatario no puede ser nulo o vacío");
        }
        this.destinatarioNombre = destinatarioNombre.trim();
    }

    public String getDestinatarioContacto() {
        return destinatarioContacto;
    }

    public void setDestinatarioContacto(String destinatarioContacto) {
        if (destinatarioContacto == null || destinatarioContacto.trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto del destinatario no puede ser nulo o vacío");
        }
        this.destinatarioContacto = destinatarioContacto.trim();
    }

    public String getDestinatarioDireccion() {
        return destinatarioDireccion;
    }

    public void setDestinatarioDireccion(String destinatarioDireccion) {
        if (destinatarioDireccion == null || destinatarioDireccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección del destinatario no puede ser nula o vacía");
        }
        this.destinatarioDireccion = destinatarioDireccion.trim();
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public void setEstado(EstadoPaquete estado) {
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
    public boolean isEnBodega() {
        return this.estado == EstadoPaquete.EN_BODEGA;
    }

    public boolean isAsignadoRuta() {
        return this.estado == EstadoPaquete.ASIGNADO_RUTA;
    }

    public boolean isEnTransito() {
        return this.estado == EstadoPaquete.EN_TRANSITO;
    }

    public boolean isEntregado() {
        return this.estado == EstadoPaquete.ENTREGADO;
    }

    public boolean isDevuelto() {
        return this.estado == EstadoPaquete.DEVUELTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paquete paquete = (Paquete) o;
        return Objects.equals(idSeguimiento, paquete.idSeguimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSeguimiento);
    }

    @Override
    public String toString() {
        return String.format("Paquete{id='%s', descripcion='%s', peso=%.2fkg, estado=%s, " +
                           "origen='%s', destino='%s'}", 
                           idSeguimiento, descripcion, pesoKg, estado, dirOrigen, dirDestino);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String idSeguimiento;
        private String descripcion;
        private BigDecimal pesoKg;
        private String dimensiones;
        private String dirOrigen;
        private String dirDestino;
        private String remitenteNombre;
        private String remitenteContacto;
        private String remitenteDireccion;
        private String destinatarioNombre;
        private String destinatarioContacto;
        private String destinatarioDireccion;
        private EstadoPaquete estado = EstadoPaquete.EN_BODEGA;

        public Builder idSeguimiento(String idSeguimiento) {
            this.idSeguimiento = idSeguimiento;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder pesoKg(BigDecimal pesoKg) {
            this.pesoKg = pesoKg;
            return this;
        }

        public Builder dimensiones(String dimensiones) {
            this.dimensiones = dimensiones;
            return this;
        }

        public Builder dirOrigen(String dirOrigen) {
            this.dirOrigen = dirOrigen;
            return this;
        }

        public Builder dirDestino(String dirDestino) {
            this.dirDestino = dirDestino;
            return this;
        }

        public Builder remitenteNombre(String remitenteNombre) {
            this.remitenteNombre = remitenteNombre;
            return this;
        }

        public Builder remitenteContacto(String remitenteContacto) {
            this.remitenteContacto = remitenteContacto;
            return this;
        }

        public Builder remitenteDireccion(String remitenteDireccion) {
            this.remitenteDireccion = remitenteDireccion;
            return this;
        }

        public Builder destinatarioNombre(String destinatarioNombre) {
            this.destinatarioNombre = destinatarioNombre;
            return this;
        }

        public Builder destinatarioContacto(String destinatarioContacto) {
            this.destinatarioContacto = destinatarioContacto;
            return this;
        }

        public Builder destinatarioDireccion(String destinatarioDireccion) {
            this.destinatarioDireccion = destinatarioDireccion;
            return this;
        }

        public Builder estado(EstadoPaquete estado) {
            this.estado = estado;
            return this;
        }

        public Paquete build() {
            return new Paquete(idSeguimiento, descripcion, pesoKg, dimensiones,
                             dirOrigen, dirDestino, remitenteNombre, remitenteContacto,
                             remitenteDireccion, destinatarioNombre, destinatarioContacto,
                             destinatarioDireccion, estado);
        }
    }
}