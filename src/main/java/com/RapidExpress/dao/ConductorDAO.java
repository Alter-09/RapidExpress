package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.TipoLicencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConductorDAO {

    public boolean create(Conductor conductor) {
        if (exists(conductor.getNumIdentificacion())) {
            System.err.println("Error: Ya existe un conductor con ID: " + conductor.getNumIdentificacion());
            return false;
        }

        // Validar que el vehículo no esté asignado a otro conductor
        if (conductor.getVehiculoAsignado() != null && !conductor.getVehiculoAsignado().isEmpty()) {
            if (isVehiculoAsignado(conductor.getVehiculoAsignado())) {
                System.err.println("Error: El vehículo " + conductor.getVehiculoAsignado() 
                                 + " ya está asignado a otro conductor");
                return false;
            }
        }

        String sql = "INSERT INTO conductor (num_identificacion, nombre_completo, tipo_licencia, " +
                     "num_contacto, estado, vehiculo_asignado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conductor.getNumIdentificacion());
            stmt.setString(2, conductor.getNombreCompleto());
            stmt.setString(3, conductor.getTipoLicencia().getCodigo());
            stmt.setString(4, conductor.getNumContacto());
            stmt.setString(5, conductor.getEstado().getDescripcion());
            stmt.setString(6, conductor.getVehiculoAsignado());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear conductor: " + e.getMessage());
            if (e.getErrorCode() == 1062) {
                System.err.println("Violación de unicidad: el vehículo ya está asignado");
            }
            return false;
        }
    }

    public Optional<Conductor> findById(String numIdentificacion) {
        String sql = "SELECT * FROM conductor WHERE num_identificacion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numIdentificacion);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToConductor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar conductor: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<Conductor> findAll() {
        List<Conductor> conductores = new ArrayList<>();
        String sql = "SELECT * FROM conductor ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                conductores.add(mapResultSetToConductor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar conductores: " + e.getMessage());
        }
        
        return conductores;
    }

    public boolean update(Conductor conductor) {
        if (!exists(conductor.getNumIdentificacion())) {
            System.err.println("Error: No existe un conductor con ID: " + conductor.getNumIdentificacion());
            return false;
        }

        // Validar asignación de vehículo si cambió
        if (conductor.getVehiculoAsignado() != null && !conductor.getVehiculoAsignado().isEmpty()) {
            Optional<Conductor> actual = findById(conductor.getNumIdentificacion());
            if (actual.isPresent() && !conductor.getVehiculoAsignado().equals(actual.get().getVehiculoAsignado())) {
                if (isVehiculoAsignado(conductor.getVehiculoAsignado())) {
                    System.err.println("Error: El vehículo " + conductor.getVehiculoAsignado() 
                                     + " ya está asignado a otro conductor");
                    return false;
                }
            }
        }

        String sql = "UPDATE conductor SET nombre_completo=?, tipo_licencia=?, num_contacto=?, " +
                     "estado=?, vehiculo_asignado=? WHERE num_identificacion=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, conductor.getNombreCompleto());
            stmt.setString(2, conductor.getTipoLicencia().getCodigo());
            stmt.setString(3, conductor.getNumContacto());
            stmt.setString(4, conductor.getEstado().getDescripcion());
            stmt.setString(5, conductor.getVehiculoAsignado());
            stmt.setString(6, conductor.getNumIdentificacion());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar conductor: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEstado(String numIdentificacion, EstadoConductor nuevoEstado) {
        if (!exists(numIdentificacion)) {
            System.err.println("Error: No existe un conductor con ID: " + numIdentificacion);
            return false;
        }

        String sql = "UPDATE conductor SET estado=? WHERE num_identificacion=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado.getDescripcion());
            stmt.setString(2, numIdentificacion);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del conductor: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarVehiculo(String numIdentificacion, String placa) {
        if (!exists(numIdentificacion)) {
            System.err.println("Error: No existe un conductor con ID: " + numIdentificacion);
            return false;
        }

        if (isVehiculoAsignado(placa)) {
            System.err.println("Error: El vehículo " + placa + " ya está asignado a otro conductor");
            return false;
        }

        String sql = "UPDATE conductor SET vehiculo_asignado=?, estado=? WHERE num_identificacion=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            stmt.setString(2, EstadoConductor.ACTIVO.getDescripcion());
            stmt.setString(3, numIdentificacion);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al asignar vehículo al conductor: " + e.getMessage());
            return false;
        }
    }

    public boolean liberarVehiculo(String numIdentificacion) {
        if (!exists(numIdentificacion)) {
            System.err.println("Error: No existe un conductor con ID: " + numIdentificacion);
            return false;
        }

        String sql = "UPDATE conductor SET vehiculo_asignado=NULL WHERE num_identificacion=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numIdentificacion);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al liberar vehículo del conductor: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String numIdentificacion) {
        if (!exists(numIdentificacion)) {
            System.err.println("Error: No existe un conductor con ID: " + numIdentificacion);
            return false;
        }

        Optional<Conductor> conductorOpt = findById(numIdentificacion);
        if (conductorOpt.isPresent() && conductorOpt.get().isEnRuta()) {
            System.err.println("Error: No se puede eliminar un conductor que está en ruta");
            return false;
        }

        String sql = "DELETE FROM conductor WHERE num_identificacion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numIdentificacion);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar conductor: " + e.getMessage());
            if (e.getErrorCode() == 1451) {
                System.err.println("No se puede eliminar: el conductor tiene rutas asociadas");
            }
            return false;
        }
    }

    public List<Conductor> findByEstado(EstadoConductor estado) {
        List<Conductor> conductores = new ArrayList<>();
        String sql = "SELECT * FROM conductor WHERE estado = ? ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                conductores.add(mapResultSetToConductor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al filtrar conductores por estado: " + e.getMessage());
        }
        
        return conductores;
    }

    public List<Conductor> findActivos() {
        return findByEstado(EstadoConductor.ACTIVO);
    }

    public List<Conductor> findDisponibles() {
        List<Conductor> conductores = new ArrayList<>();
        String sql = "SELECT * FROM conductor WHERE estado = 'Activo' AND vehiculo_asignado IS NULL " +
                     "ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                conductores.add(mapResultSetToConductor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar conductores disponibles: " + e.getMessage());
        }
        
        return conductores;
    }

    public List<Conductor> findByTipoLicencia(TipoLicencia tipoLicencia) {
        List<Conductor> conductores = new ArrayList<>();
        String sql = "SELECT * FROM conductor WHERE tipo_licencia = ? ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipoLicencia.getCodigo());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                conductores.add(mapResultSetToConductor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar conductores por tipo de licencia: " + e.getMessage());
        }
        
        return conductores;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM conductor";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar conductores: " + e.getMessage());
        }
        
        return 0;
    }

    public int countByEstado(EstadoConductor estado) {
        String sql = "SELECT COUNT(*) FROM conductor WHERE estado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar conductores por estado: " + e.getMessage());
        }
        
        return 0;
    }

    public boolean exists(String numIdentificacion) {
        String sql = "SELECT COUNT(*) FROM conductor WHERE num_identificacion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numIdentificacion);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de conductor: " + e.getMessage());
        }
        
        return false;
    }

    public boolean isVehiculoAsignado(String placa) {
        String sql = "SELECT COUNT(*) FROM conductor WHERE vehiculo_asignado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar asignación de vehículo: " + e.getMessage());
        }
        
        return false;
    }

    public Optional<Conductor> findByVehiculo(String placa) {
        String sql = "SELECT * FROM conductor WHERE vehiculo_asignado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToConductor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar conductor por vehículo: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    private Conductor mapResultSetToConductor(ResultSet rs) throws SQLException {
        Conductor conductor = new Conductor();
        conductor.setNumIdentificacion(rs.getString("num_identificacion"));
        conductor.setNombreCompleto(rs.getString("nombre_completo"));
        conductor.setTipoLicencia(TipoLicencia.fromString(rs.getString("tipo_licencia")));
        conductor.setNumContacto(rs.getString("num_contacto"));
        conductor.setEstado(EstadoConductor.fromString(rs.getString("estado")));
        conductor.setVehiculoAsignado(rs.getString("vehiculo_asignado"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            conductor.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            conductor.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return conductor;
    }
}