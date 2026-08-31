package com.RapidExpress.dao;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiculoDAO {

    /**
     * Crea un nuevo vehículo en la base de datos
     * @param vehiculo El vehículo a crear
     * @return true si se creó exitosamente, false en caso contrario
     */
    public boolean create(Vehiculo vehiculo) {
        // Verificar si ya existe
        if (exists(vehiculo.getPlaca())) {
            System.err.println("Error: Ya existe un vehículo con la placa: " + vehiculo.getPlaca());
            return false;
        }

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
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("La placa ya existe en la base de datos");
            }
            return false;
        }
    }

    /**
     * Busca un vehículo por su placa
     * @param placa La placa del vehículo
     * @return Optional con el vehículo si existe, vacío si no
     */
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

    /**
     * Obtiene todos los vehículos de la base de datos
     * @return Lista de todos los vehículos
     */
    public List<Vehiculo> findAll() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo ORDER BY marca, modelo, placa";
        
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

    /**
     * Actualiza un vehículo existente
     * @param vehiculo El vehículo con los datos actualizados
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean update(Vehiculo vehiculo) {
        // Verificar si existe
        if (!exists(vehiculo.getPlaca())) {
            System.err.println("Error: No existe un vehículo con la placa: " + vehiculo.getPlaca());
            return false;
        }

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

    /**
     * Actualiza solo el estado de un vehículo
     * @param placa La placa del vehículo
     * @param nuevoEstado El nuevo estado
     * @return true si se actualizó exitosamente, false en caso contrario
     */
    public boolean updateEstado(String placa, EstadoVehiculo nuevoEstado) {
        if (!exists(placa)) {
            System.err.println("Error: No existe un vehículo con la placa: " + placa);
            return false;
        }

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

    /**
     * Elimina un vehículo de la base de datos
     * @param placa La placa del vehículo a eliminar
     * @return true si se eliminó exitosamente, false en caso contrario
     */
    public boolean delete(String placa) {
        // Verificar si existe
        if (!exists(placa)) {
            System.err.println("Error: No existe un vehículo con la placa: " + placa);
            return false;
        }

        // Verificar si tiene conductor asignado
        Optional<Vehiculo> vehiculoOpt = findById(placa);
        if (vehiculoOpt.isPresent() && vehiculoOpt.get().isEnRuta()) {
            System.err.println("Error: No se puede eliminar un vehículo que está en ruta");
            return false;
        }

        String sql = "DELETE FROM vehiculo WHERE placa = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            // Error de foreign key constraint
            if (e.getErrorCode() == 1451) {
                System.err.println("No se puede eliminar: el vehículo tiene registros relacionados " +
                                 "(conductores asignados, rutas o mantenimientos)");
            }
            return false;
        }
    }

    /**
     * Busca vehículos por estado
     * @param estado El estado a filtrar
     * @return Lista de vehículos con el estado especificado
     */
    public List<Vehiculo> findByEstado(EstadoVehiculo estado) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE estado = ? ORDER BY marca, modelo";
        
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

    /**
     * Busca vehículos disponibles
     * @return Lista de vehículos disponibles
     */
    public List<Vehiculo> findDisponibles() {
        return findByEstado(EstadoVehiculo.DISPONIBLE);
    }

    /**
     * Busca vehículos en ruta
     * @return Lista de vehículos en ruta
     */
    public List<Vehiculo> findEnRuta() {
        return findByEstado(EstadoVehiculo.EN_RUTA);
    }

    /**
     * Busca vehículos en mantenimiento
     * @return Lista de vehículos en mantenimiento
     */
    public List<Vehiculo> findEnMantenimiento() {
        return findByEstado(EstadoVehiculo.EN_MANTENIMIENTO);
    }

    /**
     * Cuenta el total de vehículos
     * @return Número total de vehículos
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM vehiculo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar vehículos: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Cuenta vehículos por estado
     * @param estado El estado a contar
     * @return Número de vehículos con ese estado
     */
    public int countByEstado(EstadoVehiculo estado) {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE estado = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.getDescripcion());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar vehículos por estado: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Verifica si existe un vehículo con la placa especificada
     * @param placa La placa a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean exists(String placa) {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE placa = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de vehículo: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Busca vehículos por marca
     * @param marca La marca a buscar
     * @return Lista de vehículos de la marca especificada
     */
    public List<Vehiculo> findByMarca(String marca) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM vehiculo WHERE marca LIKE ? ORDER BY modelo, placa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + marca.trim() + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                vehiculos.add(mapResultSetToVehiculo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar vehículos por marca: " + e.getMessage());
        }
        
        return vehiculos;
    }

    /**
     * Mapea un ResultSet a un objeto Vehiculo
     * @param rs El ResultSet con los datos
     * @return El objeto Vehiculo mapeado
     * @throws SQLException Si ocurre un error al mapear
     */
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