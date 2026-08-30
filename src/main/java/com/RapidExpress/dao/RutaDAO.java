package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.enums.EstadoRuta;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RutaDAO {

    public boolean create(Ruta ruta) {
        // Validar que el vehículo exista y esté disponible
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        if (!vehiculoDAO.exists(ruta.getVehiculoPlaca())) {
            System.err.println("Error: No existe un vehículo con la placa: " + ruta.getVehiculoPlaca());
            return false;
        }

        // Validar que el conductor exista
        ConductorDAO conductorDAO = new ConductorDAO();
        if (!conductorDAO.exists(ruta.getConductorId())) {
            System.err.println("Error: No existe un conductor con ID: " + ruta.getConductorId());
            return false;
        }

        String sql = "INSERT INTO ruta (fecha, vehiculo_placa, conductor_id, estado) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, Date.valueOf(ruta.getFecha()));
            stmt.setString(2, ruta.getVehiculoPlaca());
            stmt.setString(3, ruta.getConductorId());
            stmt.setString(4, ruta.getEstado().getDescripcion());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ruta.setIdRuta(generatedKeys.getLong(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al crear ruta: " + e.getMessage());
            return false;
        }
    }

    public Optional<Ruta> findById(Long idRuta) {
        String sql = "SELECT * FROM ruta WHERE id_ruta = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, idRuta);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToRuta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ruta: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<Ruta> findAll() {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM ruta ORDER BY fecha DESC, id_ruta DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rutas.add(mapResultSetToRuta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar rutas: " + e.getMessage());
        }
        
        return rutas;
    }

    public boolean update(Ruta ruta) {
        if (!exists(ruta.getIdRuta())) {
            System.err.println("Error: No existe una ruta con ID: " + ruta.getIdRuta());
            return false;
        }

        String sql = "UPDATE ruta SET fecha=?, vehiculo_placa=?, conductor_id=?, estado=? WHERE id_ruta=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(ruta.getFecha()));
            stmt.setString(2, ruta.getVehiculoPlaca());
            stmt.setString(3, ruta.getConductorId());
            stmt.setString(4, ruta.getEstado().getDescripcion());
            stmt.setLong(5, ruta.getIdRuta());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar ruta: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEstado(Long idRuta, EstadoRuta nuevoEstado) {
        if (!exists(idRuta)) {
            System.err.println("Error: No existe una ruta con ID: " + idRuta);
            return false;
        }

        String sql = "UPDATE ruta SET estado=? WHERE id_ruta=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado.getDescripcion());
            stmt.setLong(2, idRuta);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de la ruta: " + e.getMessage());
            return false;
        }
    }

    public boolean iniciarRuta(Long idRuta) {
        return updateEstado(idRuta, EstadoRuta.EN_CURSO);
    }

    public boolean completarRuta(Long idRuta) {
        return updateEstado(idRuta, EstadoRuta.COMPLETADA);
    }

    public boolean cancelarRuta(Long idRuta) {
        return updateEstado(idRuta, EstadoRuta.CANCELADA);
    }

    public List<Ruta> findByEstado(EstadoRuta estado) {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE estado = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rutas.add(mapResultSetToRuta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al filtrar rutas por estado: " + e.getMessage());
        }
        
        return rutas;
    }

    public List<Ruta> findEnCurso() {
        return findByEstado(EstadoRuta.EN_CURSO);
    }

    public List<Ruta> findByFecha(LocalDate fecha) {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE fecha = ? ORDER BY id_ruta";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rutas.add(mapResultSetToRuta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar rutas por fecha: " + e.getMessage());
        }
        
        return rutas;
    }

    public List<Ruta> findByVehiculo(String placa) {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE vehiculo_placa = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rutas.add(mapResultSetToRuta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar rutas por vehículo: " + e.getMessage());
        }
        
        return rutas;
    }

    public List<Ruta> findByConductor(String conductorId) {
        List<Ruta> rutas = new ArrayList<>();
        String sql = "SELECT * FROM ruta WHERE conductor_id = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conductorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rutas.add(mapResultSetToRuta(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar rutas por conductor: " + e.getMessage());
        }
        
        return rutas;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM ruta";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar rutas: " + e.getMessage());
        }
        
        return 0;
    }

    public int countByEstado(EstadoRuta estado) {
        String sql = "SELECT COUNT(*) FROM ruta WHERE estado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar rutas por estado: " + e.getMessage());
        }
        
        return 0;
    }

    public boolean exists(Long idRuta) {
        String sql = "SELECT COUNT(*) FROM ruta WHERE id_ruta = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, idRuta);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de ruta: " + e.getMessage());
        }
        
        return false;
    }

    private Ruta mapResultSetToRuta(ResultSet rs) throws SQLException {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(rs.getLong("id_ruta"));
        ruta.setFecha(rs.getDate("fecha").toLocalDate());
        ruta.setVehiculoPlaca(rs.getString("vehiculo_placa"));
        ruta.setConductorId(rs.getString("conductor_id"));
        ruta.setEstado(EstadoRuta.fromString(rs.getString("estado")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            ruta.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            ruta.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return ruta;
    }
}