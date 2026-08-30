package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.RutaPaquete;
import com.RapidExpress.model.enums.EstadoRutaPaquete;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RutaPaqueteDAO {

    public boolean create(RutaPaquete rutaPaquete) {
        if (exists(rutaPaquete.getRutaId(), rutaPaquete.getPaqueteId())) {
            System.err.println("Error: El paquete ya está asignado a esta ruta");
            return false;
        }

        String sql = "INSERT INTO ruta_paquete (ruta_id, paquete_id, estado_paquete, orden_entrega, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaPaquete.getRutaId());
            stmt.setString(2, rutaPaquete.getPaqueteId());
            stmt.setString(3, rutaPaquete.getEstadoPaquete().getDescripcion());
            stmt.setShort(4, rutaPaquete.getOrdenEntrega());
            stmt.setString(5, rutaPaquete.getObservaciones());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear ruta_paquete: " + e.getMessage());
            if (e.getErrorCode() == 1452) {
                System.err.println("Error de integridad referencial: ruta o paquete no existen");
            }
            return false;
        }
    }

    public Optional<RutaPaquete> findById(Long rutaId, String paqueteId) {
        String sql = "SELECT * FROM ruta_paquete WHERE ruta_id = ? AND paquete_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            stmt.setString(2, paqueteId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRutaPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ruta_paquete: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<RutaPaquete> findByRutaId(Long rutaId) {
        List<RutaPaquete> lista = new ArrayList<>();
        String sql = "SELECT * FROM ruta_paquete WHERE ruta_id = ? ORDER BY orden_entrega";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToRutaPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar paquetes de la ruta: " + e.getMessage());
        }
        
        return lista;
    }

    public List<RutaPaquete> findByPaqueteId(String paqueteId) {
        List<RutaPaquete> lista = new ArrayList<>();
        String sql = "SELECT * FROM ruta_paquete WHERE paquete_id = ? ORDER BY ruta_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paqueteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToRutaPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar rutas del paquete: " + e.getMessage());
        }
        
        return lista;
    }

    public boolean update(RutaPaquete rutaPaquete) {
        if (!exists(rutaPaquete.getRutaId(), rutaPaquete.getPaqueteId())) {
            System.err.println("Error: No existe la asignación ruta-paquete");
            return false;
        }

        String sql = "UPDATE ruta_paquete SET estado_paquete=?, orden_entrega=?, observaciones=? " +
                     "WHERE ruta_id=? AND paquete_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rutaPaquete.getEstadoPaquete().getDescripcion());
            stmt.setShort(2, rutaPaquete.getOrdenEntrega());
            stmt.setString(3, rutaPaquete.getObservaciones());
            stmt.setLong(4, rutaPaquete.getRutaId());
            stmt.setString(5, rutaPaquete.getPaqueteId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar ruta_paquete: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEstado(Long rutaId, String paqueteId, EstadoRutaPaquete nuevoEstado) {
        if (!exists(rutaId, paqueteId)) {
            System.err.println("Error: No existe la asignación ruta-paquete");
            return false;
        }

        String sql = "UPDATE ruta_paquete SET estado_paquete=? WHERE ruta_id=? AND paquete_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado.getDescripcion());
            stmt.setLong(2, rutaId);
            stmt.setString(3, paqueteId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del paquete en ruta: " + e.getMessage());
            return false;
        }
    }

    public boolean marcarEntregado(Long rutaId, String paqueteId, String observacion) {
        return updateEstadoConObservacion(rutaId, paqueteId, EstadoRutaPaquete.ENTREGADO, observacion);
    }

    public boolean marcarDevuelto(Long rutaId, String paqueteId, String observacion) {
        return updateEstadoConObservacion(rutaId, paqueteId, EstadoRutaPaquete.DEVUELTO, observacion);
    }

    public boolean marcarNoEntregado(Long rutaId, String paqueteId, String observacion) {
        return updateEstadoConObservacion(rutaId, paqueteId, EstadoRutaPaquete.NO_ENTREGADO, observacion);
    }

    private boolean updateEstadoConObservacion(Long rutaId, String paqueteId, 
                                                EstadoRutaPaquete estado, String observacion) {
        if (!exists(rutaId, paqueteId)) {
            System.err.println("Error: No existe la asignación ruta-paquete");
            return false;
        }

        String sql = "UPDATE ruta_paquete SET estado_paquete=?, observaciones=? " +
                     "WHERE ruta_id=? AND paquete_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            stmt.setString(2, observacion);
            stmt.setLong(3, rutaId);
            stmt.setString(4, paqueteId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(Long rutaId, String paqueteId) {
        if (!exists(rutaId, paqueteId)) {
            System.err.println("Error: No existe la asignación ruta-paquete");
            return false;
        }

        String sql = "DELETE FROM ruta_paquete WHERE ruta_id=? AND paquete_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            stmt.setString(2, paqueteId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar ruta_paquete: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteByRutaId(Long rutaId) {
        String sql = "DELETE FROM ruta_paquete WHERE ruta_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar paquetes de la ruta: " + e.getMessage());
            return false;
        }
    }

    public int countByRutaId(Long rutaId) {
        String sql = "SELECT COUNT(*) FROM ruta_paquete WHERE ruta_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar paquetes de la ruta: " + e.getMessage());
        }
        
        return 0;
    }

    public int countByEstado(Long rutaId, EstadoRutaPaquete estado) {
        String sql = "SELECT COUNT(*) FROM ruta_paquete WHERE ruta_id = ? AND estado_paquete = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            stmt.setString(2, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar paquetes por estado: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Calcula el peso total de los paquetes asignados a una ruta
     */
    public BigDecimal getPesoTotalRuta(Long rutaId) {
        String sql = "SELECT COALESCE(SUM(p.peso_kg), 0) AS peso_total " +
                     "FROM ruta_paquete rp JOIN paquete p ON rp.paquete_id = p.id_seguimiento " +
                     "WHERE rp.ruta_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("peso_total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular peso total: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }

    public boolean exists(Long rutaId, String paqueteId) {
        String sql = "SELECT COUNT(*) FROM ruta_paquete WHERE ruta_id = ? AND paquete_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, rutaId);
            stmt.setString(2, paqueteId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia: " + e.getMessage());
        }
        
        return false;
    }

    private RutaPaquete mapResultSetToRutaPaquete(ResultSet rs) throws SQLException {
        RutaPaquete rp = new RutaPaquete();
        rp.setRutaId(rs.getLong("ruta_id"));
        rp.setPaqueteId(rs.getString("paquete_id"));
        rp.setEstadoPaquete(EstadoRutaPaquete.fromString(rs.getString("estado_paquete")));
        rp.setOrdenEntrega(rs.getShort("orden_entrega"));
        rp.setObservaciones(rs.getString("observaciones"));
        
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        if (actualizadoEn != null) {
            rp.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        return rp;
    }
}