package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.enums.TipoMantenimiento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MantenimientoDAO {

    public boolean create(Mantenimiento mantenimiento) {
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        if (!vehiculoDAO.exists(mantenimiento.getVehiculoPlaca())) {
            System.err.println("Error: No existe un vehículo con la placa: " + mantenimiento.getVehiculoPlaca());
            return false;
        }

        String sql = "INSERT INTO mantenimiento (vehiculo_placa, fecha, tipo, descripcion, " +
                     "costo, kilometraje, proveedor) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, mantenimiento.getVehiculoPlaca());
            stmt.setDate(2, Date.valueOf(mantenimiento.getFecha()));
            stmt.setString(3, mantenimiento.getTipo().getTipo());
            stmt.setString(4, mantenimiento.getDescripcion());
            stmt.setBigDecimal(5, mantenimiento.getCosto());
            
            if (mantenimiento.getKilometraje() != null) {
                stmt.setInt(6, mantenimiento.getKilometraje());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setString(7, mantenimiento.getProveedor());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    mantenimiento.setIdMantenimiento(generatedKeys.getLong(1));
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al crear mantenimiento: " + e.getMessage());
            return false;
        }
    }

    public Optional<Mantenimiento> findById(Long idMantenimiento) {
        String sql = "SELECT * FROM mantenimiento WHERE id_mantenimiento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, idMantenimiento);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimiento: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    public List<Mantenimiento> findAll() {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mantenimientos.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar mantenimientos: " + e.getMessage());
        }
        
        return mantenimientos;
    }

    public boolean update(Mantenimiento mantenimiento) {
        if (!exists(mantenimiento.getIdMantenimiento())) {
            System.err.println("Error: No existe un mantenimiento con ID: " + mantenimiento.getIdMantenimiento());
            return false;
        }

        String sql = "UPDATE mantenimiento SET vehiculo_placa=?, fecha=?, tipo=?, descripcion=?, " +
                     "costo=?, kilometraje=?, proveedor=? WHERE id_mantenimiento=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, mantenimiento.getVehiculoPlaca());
            stmt.setDate(2, Date.valueOf(mantenimiento.getFecha()));
            stmt.setString(3, mantenimiento.getTipo().getTipo());
            stmt.setString(4, mantenimiento.getDescripcion());
            stmt.setBigDecimal(5, mantenimiento.getCosto());
            
            if (mantenimiento.getKilometraje() != null) {
                stmt.setInt(6, mantenimiento.getKilometraje());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setString(7, mantenimiento.getProveedor());
            stmt.setLong(8, mantenimiento.getIdMantenimiento());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar mantenimiento: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(Long idMantenimiento) {
        if (!exists(idMantenimiento)) {
            System.err.println("Error: No existe un mantenimiento con ID: " + idMantenimiento);
            return false;
        }

        String sql = "DELETE FROM mantenimiento WHERE id_mantenimiento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, idMantenimiento);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar mantenimiento: " + e.getMessage());
            return false;
        }
    }

    public List<Mantenimiento> findByVehiculo(String placa) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento WHERE vehiculo_placa = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mantenimientos.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimientos por vehículo: " + e.getMessage());
        }
        
        return mantenimientos;
    }

    public List<Mantenimiento> findByTipo(TipoMantenimiento tipo) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento WHERE tipo = ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.getTipo());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mantenimientos.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimientos por tipo: " + e.getMessage());
        }
        
        return mantenimientos;
    }

    public List<Mantenimiento> findByFecha(LocalDate fecha) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento WHERE fecha = ? ORDER BY vehiculo_placa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fecha));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mantenimientos.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimientos por fecha: " + e.getMessage());
        }
        
        return mantenimientos;
    }

    public List<Mantenimiento> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        String sql = "SELECT * FROM mantenimiento WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                mantenimientos.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar mantenimientos por rango de fechas: " + e.getMessage());
        }
        
        return mantenimientos;
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM mantenimiento";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar mantenimientos: " + e.getMessage());
        }
        
        return 0;
    }

    public int countByVehiculo(String placa) {
        String sql = "SELECT COUNT(*) FROM mantenimiento WHERE vehiculo_placa = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar mantenimientos por vehículo: " + e.getMessage());
        }
        
        return 0;
    }

    public boolean exists(Long idMantenimiento) {
        String sql = "SELECT COUNT(*) FROM mantenimiento WHERE id_mantenimiento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, idMantenimiento);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia: " + e.getMessage());
        }
        
        return false;
    }

    private Mantenimiento mapResultSetToMantenimiento(ResultSet rs) throws SQLException {
        Mantenimiento mant = new Mantenimiento();
        mant.setIdMantenimiento(rs.getLong("id_mantenimiento"));
        mant.setVehiculoPlaca(rs.getString("vehiculo_placa"));
        mant.setFecha(rs.getDate("fecha").toLocalDate());
        mant.setTipo(TipoMantenimiento.fromString(rs.getString("tipo")));
        mant.setDescripcion(rs.getString("descripcion"));
        mant.setCosto(rs.getBigDecimal("costo"));
        
        int kilometraje = rs.getInt("kilometraje");
        if (!rs.wasNull()) {
            mant.setKilometraje(kilometraje);
        }
        
        mant.setProveedor(rs.getString("proveedor"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            mant.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return mant;
    }
}