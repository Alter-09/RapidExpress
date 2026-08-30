package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Auditoria;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditoriaDAO {

    /**
     * Registrar una nueva operación en la auditoría (append-only)
     */
    public boolean create(Auditoria auditoria) {
        String sql = "INSERT INTO auditoria (timestamp, operacion, entidad, entidad_id, " +
                     "usuario, detalles, ip_origen) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(auditoria.getTimestamp()));
            stmt.setString(2, auditoria.getOperacion());
            stmt.setString(3, auditoria.getEntidad());
            stmt.setString(4, auditoria.getEntidadId());
            stmt.setString(5, auditoria.getUsuario());
            stmt.setString(6, auditoria.getDetalles());
            stmt.setString(7, auditoria.getIpOrigen());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    auditoria.setIdAuditoria(generatedKeys.getLong(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra una operación con datos simplificados
     */
    public boolean log(String operacion, String entidad, String entidadId, 
                       String usuario, String detalles, String ipOrigen) {
        Auditoria auditoria = new Auditoria();
        auditoria.setOperacion(operacion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setUsuario(usuario);
        auditoria.setDetalles(detalles);
        auditoria.setIpOrigen(ipOrigen);
        return create(auditoria);
    }

    public Optional<Auditoria> findById(Long idAuditoria) {
        String sql = "SELECT * FROM auditoria WHERE id_auditoria = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, idAuditoria);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar auditoría: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<Auditoria> findAll() {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar auditorías: " + e.getMessage());
        }
        
        return lista;
    }

    public List<Auditoria> findByEntidad(String entidad) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria WHERE entidad = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, entidad);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar auditorías por entidad: " + e.getMessage());
        }
        
        return lista;
    }

    public List<Auditoria> findByEntidadId(String entidad, String entidadId) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria WHERE entidad = ? AND entidad_id = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, entidad);
            stmt.setString(2, entidadId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar auditorías por entidad e ID: " + e.getMessage());
        }
        
        return lista;
    }

    public List<Auditoria> findByOperacion(String operacion) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria WHERE operacion = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, operacion);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar auditorías por operación: " + e.getMessage());
        }
        
        return lista;
    }

    public List<Auditoria> findByUsuario(String usuario) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria WHERE usuario = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar auditorías por usuario: " + e.getMessage());
        }
        
        return lista;
    }

    public List<Auditoria> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar auditorías por rango de fechas: " + e.getMessage());
        }
        
        return lista;
    }

    public List<Auditoria> findUltimas(int cantidad) {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cantidad);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToAuditoria(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar últimas auditorías: " + e.getMessage());
        }
        
        return lista;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM auditoria";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar auditorías: " + e.getMessage());
        }
        
        return 0;
    }

    public int countByEntidad(String entidad) {
        String sql = "SELECT COUNT(*) FROM auditoria WHERE entidad = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, entidad);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar auditorías por entidad: " + e.getMessage());
        }
        
        return 0;
    }

    private Auditoria mapResultSetToAuditoria(ResultSet rs) throws SQLException {
        Auditoria aud = new Auditoria();
        aud.setIdAuditoria(rs.getLong("id_auditoria"));
        
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            aud.setTimestamp(timestamp.toLocalDateTime());
        }
        
        aud.setOperacion(rs.getString("operacion"));
        aud.setEntidad(rs.getString("entidad"));
        aud.setEntidadId(rs.getString("entidad_id"));
        aud.setUsuario(rs.getString("usuario"));
        aud.setDetalles(rs.getString("detalles"));
        aud.setIpOrigen(rs.getString("ip_origen"));
        
        return aud;
    }
}