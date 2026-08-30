package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiculoDAO {

    public boolean create(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculo (placa, marca, modelo, anio_fabricacion, " +
                     "capacidad_max_kg, estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehiculo.getPlaca());
            stmt.setString(2, vehiculo.getMarca());
            stmt.setString(3, vehiculo.getModelo());
            stmt.setInt(4, vehiculo.getAnioFabricacion());
            stmt.setBigDecimal(5, vehiculo.getCapacidadMaxKg());
            stmt.setString(6, vehiculo.getEstado().getDescripcion());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear vehículo: " + e.getMessage());
            return false;
        }
    }

    public Optional<Vehiculo> findById(String placa) {
        String sql = "SELECT * FROM vehiculo WHERE placa = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToVehiculo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar vehículo: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<Vehiculo> findAll() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo ORDER BY marca, modelo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                vehiculos.add(mapResultSetToVehiculo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar vehículos: " + e.getMessage());
        }
        
        return vehiculos;
    }

    public boolean update(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculo SET marca=?, modelo=?, anio_fabricacion=?, " +
                     "capacidad_max_kg=?, estado=? WHERE placa=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehiculo.getMarca());
            stmt.setString(2, vehiculo.getModelo());
            stmt.setInt(3, vehiculo.getAnioFabricacion());
            stmt.setBigDecimal(4, vehiculo.getCapacidadMaxKg());
            stmt.setString(5, vehiculo.getEstado().getDescripcion());
            stmt.setString(6, vehiculo.getPlaca());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar vehículo: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEstado(String placa, EstadoVehiculo nuevoEstado) {
        String sql = "UPDATE vehiculo SET estado=? WHERE placa=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado.getDescripcion());
            stmt.setString(2, placa);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del vehículo: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String placa) {
        String sql = "DELETE FROM vehiculo WHERE placa = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            return false;
        }
    }

    public List<Vehiculo> findByEstado(EstadoVehiculo estado) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE estado = ? ORDER BY marca";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehiculos.add(mapResultSetToVehiculo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al filtrar vehículos por estado: " + e.getMessage());
        }
        
        return vehiculos;
    }

    private Vehiculo mapResultSetToVehiculo(ResultSet rs) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(rs.getString("placa"));
        vehiculo.setMarca(rs.getString("marca"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setAnioFabricacion(rs.getInt("anio_fabricacion"));
        vehiculo.setCapacidadMaxKg(rs.getBigDecimal("capacidad_max_kg"));
        vehiculo.setEstado(EstadoVehiculo.fromString(rs.getString("estado")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            vehiculo.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            vehiculo.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return vehiculo;
    }
}