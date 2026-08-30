package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaqueteDAO {

    public boolean create(Paquete paquete) {
        if (exists(paquete.getIdSeguimiento())) {
            System.err.println("Error: Ya existe un paquete con ID: " + paquete.getIdSeguimiento());
            return false;
        }

        String sql = "INSERT INTO paquete (id_seguimiento, descripcion, peso_kg, dimensiones, " +
                     "dir_origen, dir_destino, remitente_nombre, remitente_contacto, remitente_direccion, " +
                     "destinatario_nombre, destinatario_contacto, destinatario_direccion, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paquete.getIdSeguimiento());
            stmt.setString(2, paquete.getDescripcion());
            stmt.setBigDecimal(3, paquete.getPesoKg());
            stmt.setString(4, paquete.getDimensiones());
            stmt.setString(5, paquete.getDirOrigen());
            stmt.setString(6, paquete.getDirDestino());
            stmt.setString(7, paquete.getRemitenteNombre());
            stmt.setString(8, paquete.getRemitenteContacto());
            stmt.setString(9, paquete.getRemitenteDireccion());
            stmt.setString(10, paquete.getDestinatarioNombre());
            stmt.setString(11, paquete.getDestinatarioContacto());
            stmt.setString(12, paquete.getDestinatarioDireccion());
            stmt.setString(13, paquete.getEstado().getDescripcion());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear paquete: " + e.getMessage());
            return false;
        }
    }

    public Optional<Paquete> findById(String idSeguimiento) {
        String sql = "SELECT * FROM paquete WHERE id_seguimiento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idSeguimiento);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar paquete: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<Paquete> findAll() {
        List<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT * FROM paquete ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                paquetes.add(mapResultSetToPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar paquetes: " + e.getMessage());
        }
        
        return paquetes;
    }

    public boolean update(Paquete paquete) {
        if (!exists(paquete.getIdSeguimiento())) {
            System.err.println("Error: No existe un paquete con ID: " + paquete.getIdSeguimiento());
            return false;
        }

        String sql = "UPDATE paquete SET descripcion=?, peso_kg=?, dimensiones=?, " +
                     "dir_origen=?, dir_destino=?, remitente_nombre=?, remitente_contacto=?, " +
                     "remitente_direccion=?, destinatario_nombre=?, destinatario_contacto=?, " +
                     "destinatario_direccion=?, estado=? WHERE id_seguimiento=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paquete.getDescripcion());
            stmt.setBigDecimal(2, paquete.getPesoKg());
            stmt.setString(3, paquete.getDimensiones());
            stmt.setString(4, paquete.getDirOrigen());
            stmt.setString(5, paquete.getDirDestino());
            stmt.setString(6, paquete.getRemitenteNombre());
            stmt.setString(7, paquete.getRemitenteContacto());
            stmt.setString(8, paquete.getRemitenteDireccion());
            stmt.setString(9, paquete.getDestinatarioNombre());
            stmt.setString(10, paquete.getDestinatarioContacto());
            stmt.setString(11, paquete.getDestinatarioDireccion());
            stmt.setString(12, paquete.getEstado().getDescripcion());
            stmt.setString(13, paquete.getIdSeguimiento());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar paquete: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEstado(String idSeguimiento, EstadoPaquete nuevoEstado) {
        if (!exists(idSeguimiento)) {
            System.err.println("Error: No existe un paquete con ID: " + idSeguimiento);
            return false;
        }

        String sql = "UPDATE paquete SET estado=? WHERE id_seguimiento=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado.getDescripcion());
            stmt.setString(2, idSeguimiento);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del paquete: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String idSeguimiento) {
        if (!exists(idSeguimiento)) {
            System.err.println("Error: No existe un paquete con ID: " + idSeguimiento);
            return false;
        }

        Optional<Paquete> paqueteOpt = findById(idSeguimiento);
        if (paqueteOpt.isPresent() && paqueteOpt.get().isEnTransito()) {
            System.err.println("Error: No se puede eliminar un paquete que está en tránsito");
            return false;
        }

        String sql = "DELETE FROM paquete WHERE id_seguimiento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idSeguimiento);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar paquete: " + e.getMessage());
            if (e.getErrorCode() == 1451) {
                System.err.println("No se puede eliminar: el paquete está asignado a una ruta");
            }
            return false;
        }
    }

    public List<Paquete> findByEstado(EstadoPaquete estado) {
        List<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT * FROM paquete WHERE estado = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paquetes.add(mapResultSetToPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al filtrar paquetes por estado: " + e.getMessage());
        }
        
        return paquetes;
    }

    public List<Paquete> findEnBodega() {
        return findByEstado(EstadoPaquete.EN_BODEGA);
    }

    public List<Paquete> findEnTransito() {
        return findByEstado(EstadoPaquete.EN_TRANSITO);
    }

    public List<Paquete> findEntregados() {
        return findByEstado(EstadoPaquete.ENTREGADO);
    }

    public List<Paquete> findByRemitente(String nombreRemitente) {
        List<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT * FROM paquete WHERE remitente_nombre LIKE ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombreRemitente.trim() + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paquetes.add(mapResultSetToPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar paquetes por remitente: " + e.getMessage());
        }
        
        return paquetes;
    }

    public List<Paquete> findByDestinatario(String nombreDestinatario) {
        List<Paquete> paquetes = new ArrayList<>();
        String sql = "SELECT * FROM paquete WHERE destinatario_nombre LIKE ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombreDestinatario.trim() + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                paquetes.add(mapResultSetToPaquete(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar paquetes por destinatario: " + e.getMessage());
        }
        
        return paquetes;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM paquete";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar paquetes: " + e.getMessage());
        }
        
        return 0;
    }

    public int countByEstado(EstadoPaquete estado) {
        String sql = "SELECT COUNT(*) FROM paquete WHERE estado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar paquetes por estado: " + e.getMessage());
        }
        
        return 0;
    }

    public boolean exists(String idSeguimiento) {
        String sql = "SELECT COUNT(*) FROM paquete WHERE id_seguimiento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idSeguimiento);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de paquete: " + e.getMessage());
        }
        
        return false;
    }

    private Paquete mapResultSetToPaquete(ResultSet rs) throws SQLException {
        Paquete paquete = new Paquete();
        paquete.setIdSeguimiento(rs.getString("id_seguimiento"));
        paquete.setDescripcion(rs.getString("descripcion"));
        paquete.setPesoKg(rs.getBigDecimal("peso_kg"));
        paquete.setDimensiones(rs.getString("dimensiones"));
        paquete.setDirOrigen(rs.getString("dir_origen"));
        paquete.setDirDestino(rs.getString("dir_destino"));
        paquete.setRemitenteNombre(rs.getString("remitente_nombre"));
        paquete.setRemitenteContacto(rs.getString("remitente_contacto"));
        paquete.setRemitenteDireccion(rs.getString("remitente_direccion"));
        paquete.setDestinatarioNombre(rs.getString("destinatario_nombre"));
        paquete.setDestinatarioContacto(rs.getString("destinatario_contacto"));
        paquete.setDestinatarioDireccion(rs.getString("destinatario_direccion"));
        paquete.setEstado(EstadoPaquete.fromString(rs.getString("estado")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            paquete.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            paquete.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return paquete;
    }
}