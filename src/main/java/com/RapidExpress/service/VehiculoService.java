package com.RapidExpress.service;

import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.exception.EntidadDuplicadaException;
import com.RapidExpress.exception.EntidadNoEncontradaException;
import com.RapidExpress.exception.EstadoInvalidoException;
import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Capa de servicios y lógica de negocio para la gestión del parque automotor.
 */
public class VehiculoService {

    private final VehiculoDAO vehiculoDAO;
    private final AuditoriaService auditoriaService;

    public VehiculoService() {
        this.vehiculoDAO = new VehiculoDAO();
        this.auditoriaService = new AuditoriaService();
    }

    public VehiculoService(VehiculoDAO vehiculoDAO, AuditoriaService auditoriaService) {
        this.vehiculoDAO = vehiculoDAO;
        this.auditoriaService = auditoriaService;
    }

    public void registrarVehiculo(Vehiculo vehiculo, String usuario) throws ValidacionException, EntidadDuplicadaException {
        validarVehiculo(vehiculo);

        if (vehiculoDAO.exists(vehiculo.getPlaca())) {
            throw new EntidadDuplicadaException("Vehículo", vehiculo.getPlaca());
        }

        boolean creado = vehiculoDAO.create(vehiculo);
        if (!creado) {
            throw new ValidacionException("No se pudo registrar el vehículo en la base de datos.");
        }

        auditoriaService.registrar("CREATE", "vehiculo", vehiculo.getPlaca(), usuario,
                String.format("Marca: %s, Modelo: %s, Año: %d, Capacidad: %.2f kg",
                        vehiculo.getMarca(), vehiculo.getModelo(), vehiculo.getAnioFabricacion(), vehiculo.getCapacidadMaxKg()));
    }

    public void actualizarVehiculo(Vehiculo vehiculo, String usuario) throws EntidadNoEncontradaException, ValidacionException {
        validarVehiculo(vehiculo);

        Vehiculo actual = obtenerVehiculo(vehiculo.getPlaca());
        boolean actualizado = vehiculoDAO.update(vehiculo);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el vehículo con placa: " + vehiculo.getPlaca());
        }

        auditoriaService.registrar("UPDATE", "vehiculo", vehiculo.getPlaca(), usuario,
                String.format("Actualización datos: Marca=%s, Modelo=%s, Capacidad=%.2f kg",
                        vehiculo.getMarca(), vehiculo.getModelo(), vehiculo.getCapacidadMaxKg()));
    }

    public void cambiarEstado(String placa, EstadoVehiculo nuevoEstado, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Vehiculo vehiculo = obtenerVehiculo(placa);

        if (vehiculo.isEnRuta() && nuevoEstado != EstadoVehiculo.EN_RUTA) {
            throw new EstadoInvalidoException("No se puede cambiar el estado de un vehículo que se encuentra En Ruta.");
        }

        boolean actualizado = vehiculoDAO.updateEstado(placa, nuevoEstado);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el estado del vehículo.");
        }

        auditoriaService.registrar("STATE_CHANGE", "vehiculo", placa, usuario,
                String.format("Estado anterior: %s -> Nuevo estado: %s", vehiculo.getEstado(), nuevoEstado));
    }

    public void eliminarVehiculo(String placa, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Vehiculo vehiculo = obtenerVehiculo(placa);

        if (vehiculo.isEnRuta()) {
            throw new EstadoInvalidoException("No se puede eliminar un vehículo que está actualmente En Ruta.");
        }

        boolean eliminado = vehiculoDAO.delete(placa);
        if (!eliminado) {
            throw new ValidacionException("No se pudo eliminar el vehículo. Puede tener rutas o mantenimientos asociados.");
        }

        auditoriaService.registrar("DELETE", "vehiculo", placa, usuario, "Vehículo eliminado del sistema.");
    }

    public Vehiculo obtenerVehiculo(String placa) throws EntidadNoEncontradaException {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa no puede ser nula o vacía.");
        }
        return vehiculoDAO.findById(placa.toUpperCase().trim())
                .orElseThrow(() -> new EntidadNoEncontradaException("Vehículo", placa));
    }

    public List<Vehiculo> listarTodos() {
        return vehiculoDAO.findAll();
    }

    public List<Vehiculo> listarDisponibles() {
        return vehiculoDAO.findDisponibles();
    }

    public List<Vehiculo> listarEnRuta() {
        return vehiculoDAO.findEnRuta();
    }

    public List<Vehiculo> listarEnMantenimiento() {
        return vehiculoDAO.findEnMantenimiento();
    }

    public List<Vehiculo> buscarPorMarca(String marca) {
        return vehiculoDAO.findByMarca(marca);
    }

    private void validarVehiculo(Vehiculo v) throws ValidacionException {
        if (v == null) {
            throw new ValidacionException("El vehículo no puede ser nulo.");
        }
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty()) {
            throw new ValidacionException("La placa es obligatoria.");
        }
        if (v.getMarca() == null || v.getMarca().trim().isEmpty()) {
            throw new ValidacionException("La marca es obligatoria.");
        }
        if (v.getModelo() == null || v.getModelo().trim().isEmpty()) {
            throw new ValidacionException("El modelo es obligatorio.");
        }
        if (v.getAnioFabricacion() < 1990 || v.getAnioFabricacion() > 2030) {
            throw new ValidacionException("El año de fabricación debe estar entre 1990 y 2030.");
        }
        if (v.getCapacidadMaxKg() == null || v.getCapacidadMaxKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("La capacidad máxima de carga en kg debe ser mayor a 0.");
        }
    }
}
