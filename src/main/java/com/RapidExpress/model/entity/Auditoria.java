package com.RapidExpress.model.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Auditoria {
    private Long idAuditoria;
    private LocalDateTime timestamp;
    private String operacion;
    private String entidad;
    private String entidadId;
    private String usuario;
    private String detalles; // JSON como String
    private String ipOrigen;

    // Constructor vacío
    public Auditoria() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor completo
    public Auditoria(Long idAuditoria, LocalDateTime timestamp, String operacion,
                     String entidad, String entidadId, String usuario,
                     String detalles, String ipOrigen) {
        this.idAuditoria = idAuditoria;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.operacion = operacion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.usuario = usuario;
        this.detalles = detalles;
        this.ipOrigen = ipOrigen;
    }

    // Getters y Setters
    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        if (operacion == null || operacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La operación no puede ser nula o vacía");
        }
        this.operacion = operacion.trim();
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        if (entidad == null || entidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La entidad no puede ser nula o vacía");
        }
        this.entidad = entidad.trim();
    }

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String entidadId) {
        if (entidadId == null || entidadId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de entidad no puede ser nulo o vacío");
        }
        this.entidadId = entidadId.trim();
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario != null ? usuario.trim() : null;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen != null ? ipOrigen.trim() : null;
    }

    // Métodos de utilidad
    public boolean isOperacionCreate() {
        return "CREATE".equalsIgnoreCase(operacion) || operacion.toUpperCase().startsWith("CREATE_");
    }

    public boolean isOperacionUpdate() {
        return "UPDATE".equalsIgnoreCase(operacion) || operacion.toUpperCase().startsWith("UPDATE_");
    }

    public boolean isOperacionDelete() {
        return "DELETE".equalsIgnoreCase(operacion) || operacion.toUpperCase().startsWith("DELETE_");
    }

    public boolean isOperacionStateChange() {
        return operacion.toUpperCase().contains("STATE_CHANGE");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auditoria auditoria = (Auditoria) o;
        return Objects.equals(idAuditoria, auditoria.idAuditoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAuditoria);
    }

    @Override
    public String toString() {
        return String.format("Auditoria{id=%d, timestamp=%s, operacion='%s', entidad='%s', " +
                           "entidadId='%s', usuario='%s'}", 
                           idAuditoria, timestamp, operacion, entidad, entidadId, usuario);
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long idAuditoria;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String operacion;
        private String entidad;
        private String entidadId;
        private String usuario;
        private String detalles;
        private String ipOrigen;

        public Builder idAuditoria(Long idAuditoria) {
            this.idAuditoria = idAuditoria;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder operacion(String operacion) {
            this.operacion = operacion;
            return this;
        }

        public Builder entidad(String entidad) {
            this.entidad = entidad;
            return this;
        }

        public Builder entidadId(String entidadId) {
            this.entidadId = entidadId;
            return this;
        }

        public Builder usuario(String usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder detalles(String detalles) {
            this.detalles = detalles;
            return this;
        }

        public Builder ipOrigen(String ipOrigen) {
            this.ipOrigen = ipOrigen;
            return this;
        }

        public Auditoria build() {
            return new Auditoria(idAuditoria, timestamp, operacion, entidad, 
                               entidadId, usuario, detalles, ipOrigen);
        }
    }
}