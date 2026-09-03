package com.RapidExpress.service;

import com.RapidExpress.dao.ConductorDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.exception.EntidadDuplicadaException;
import com.RapidExpress.exception.EntidadNoEncontradaException;
import com.RapidExpress.exception.EstadoInvalidoException;
import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicios y lógica de negocio para la gestión del personal de conducción.
 */
public class ConductorService {

    private final ConductorDAO conductorDAO;
    private final VehiculoDAO vehiculoDAO;
    private final AuditoriaService auditoriaService;

    public ConductorService() {
        this.conductorDAO = new ConductorDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.auditoriaService = new AuditoriaService();
    }

    public ConductorService(ConductorDAO conductorDAO, VehiculoDAO vehiculoDAO, AuditoriaService auditoriaService) {
        this.conductorDAO = conductorDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.auditoriaService = auditoriaService;
    }

    public void registrarConductor(Conductor conductor, String usuario) throws ValidacionException, EntidadDuplicadaException {
        validarConductor(conductor);

        if (conductorDAO.exists(conductor.getNumIdentificacion())) {
            throw new EntidadDuplicadaException("Conductor", conductor.getNumIdentificacion());
        }

        // Si se indicó vehículo inicial, validar que exista y esté disponible
        if (conductor.tieneVehiculoAsignado()) {
            validarVehiculoParaAsignar(conductor.getVehiculoAsignado(), null);
        }

        boolean creado = conductorDAO.create(conductor);
        if (!creado) {
            throw new ValidacionException("No se pudo registrar el conductor en la base de datos.");
        }

        auditoriaService.registrar("CREATE", "conductor", conductor.getNumIdentificacion(), usuario,
                String.format("Nombre: %s, Licencia: %s, Contacto: %s",
                        conductor.getNombreCompleto(), conductor.getTipoLicencia(), conductor.getNumContacto()));
    }

    public void actualizarConductor(Conductor conductor, String usuario) throws EntidadNoEncontradaException, ValidacionException {
        validarConductor(conductor);

        Conductor actual = obtenerConductor(conductor.getNumIdentificacion());

        if (conductor.tieneVehiculoAsignado() && !conductor.getVehiculoAsignado().equalsIgnoreCase(actual.getVehiculoAsignado())) {
            validarVehiculoParaAsignar(conductor.getVehiculoAsignado(), conductor.getNumIdentificacion());
        }

        boolean actualizado = conductorDAO.update(conductor);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el conductor.");
        }

        auditoriaService.registrar("UPDATE", "conductor", conductor.getNumIdentificacion(), usuario,
                String.format("Actualizado: %s, Licencia: %s", conductor.getNombreCompleto(), conductor.getTipoLicencia()));
    }

    public void cambiarEstado(String numIdentificacion, EstadoConductor nuevoEstado, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Conductor conductor = obtenerConductor(numIdentificacion);

        if (conductor.isEnRuta() && nuevoEstado != EstadoConductor.EN_RUTA) {
            throw new EstadoInvalidoException("No se puede cambiar el estado de un conductor que está actualmente En Ruta.");
        }

        boolean actualizado = conductorDAO.updateEstado(numIdentificacion, nuevoEstado);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el estado del conductor.");
        }

        auditoriaService.registrar("STATE_CHANGE", "conductor", numIdentificacion, usuario,
                String.format("Estado: %s -> %s", conductor.getEstado(), nuevoEstado));
    }

    public void asignarVehiculo(String numIdentificacion, String placa, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Conductor conductor = obtenerConductor(numIdentificacion);

        if (conductor.isEnRuta()) {
            throw new EstadoInvalidoException("El conductor está en ruta y no puede ser reasignado en este momento.");
        }
        if (conductor.isDeVacaciones() || conductor.isInactivo()) {
            throw new EstadoInvalidoException("Solo conductores con estado Activo pueden recibir vehículos asignados.");
        }

        validarVehiculoParaAsignar(placa, numIdentificacion);

        boolean asignado = conductorDAO.asignarVehiculo(numIdentificacion, placa);
        if (!asignado) {
            throw new ValidacionException("No se pudo asignar el vehículo al conductor.");
        }

        auditoriaService.registrar("ASSIGN_VEHICLE", "conductor", numIdentificacion, usuario,
                String.format("Vehículo asignado: %s", placa));
    }

    public void liberarVehiculo(String numIdentificacion, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Conductor conductor = obtenerConductor(numIdentificacion);

        if (conductor.isEnRuta()) {
            throw new EstadoInvalidoException("No se puede desasignar el vehículo de un conductor que está En Ruta.");
        }
        if (!conductor.tieneVehiculoAsignado()) {
            throw new ValidacionException("El conductor no tiene ningún vehículo asignado actualmente.");
        }

        String vehiculoAnterior = conductor.getVehiculoAsignado();
        boolean liberado = conductorDAO.liberarVehiculo(numIdentificacion);
        if (!liberado) {
            throw new ValidacionException("No se pudo liberar el vehículo del conductor.");
        }

        auditoriaService.registrar("UNASSIGN_VEHICLE", "conductor", numIdentificacion, usuario,
                String.format("Vehículo liberado: %s", vehiculoAnterior));
    }

    public void eliminarConductor(String numIdentificacion, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Conductor conductor = obtenerConductor(numIdentificacion);

        if (conductor.isEnRuta()) {
            throw new EstadoInvalidoException("No se puede eliminar un conductor en ruta.");
        }

        boolean eliminado = conductorDAO.delete(numIdentificacion);
        if (!eliminado) {
            throw new ValidacionException("No se pudo eliminar el conductor. Puede tener rutas asociadas.");
        }

        auditoriaService.registrar("DELETE", "conductor", numIdentificacion, usuario, "Conductor eliminado.");
    }

    public Conductor obtenerConductor(String numIdentificacion) throws EntidadNoEncontradaException {
        if (numIdentificacion == null || numIdentificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de identificación no puede ser nulo o vacío.");
        }
        return conductorDAO.findById(numIdentificacion.trim())
                .orElseThrow(() -> new EntidadNoEncontradaException("Conductor", numIdentificacion));
    }

    public List<Conductor> listarTodos() {
        return conductorDAO.findAll();
    }

    public List<Conductor> listarActivos() {
        return conductorDAO.findActivos();
    }

    public List<Conductor> listarDisponibles() {
        return conductorDAO.findDisponibles();
    }

    private void validarVehiculoParaAsignar(String placa, String conductorActualId) throws ValidacionException {
        Optional<Vehiculo> vehiculoOpt = vehiculoDAO.findById(placa);
        if (vehiculoOpt.isEmpty()) {
            throw new ValidacionException("El vehículo con placa " + placa + " no existe.");
        }

        Vehiculo vehiculo = vehiculoOpt.get();
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new ValidacionException("El vehículo " + placa + " no está disponible (Estado actual: " + vehiculo.getEstado() + ").");
        }

        Optional<Conductor> asignado = conductorDAO.findByVehiculo(placa);
        if (asignado.isPresent()) {
            if (conductorActualId == null || !asignado.get().getNumIdentificacion().equals(conductorActualId)) {
                throw new ValidacionException("El vehículo " + placa + " ya está asignado al conductor " + asignado.get().getNombreCompleto());
            }
        }
    }

    private void validarConductor(Conductor c) throws ValidacionException {
        if (c == null) {
            throw new ValidacionException("El conductor no puede ser nulo.");
        }
        if (c.getNumIdentificacion() == null || c.getNumIdentificacion().trim().isEmpty()) {
            throw new ValidacionException("El número de identificación es obligatorio.");
        }
        if (c.getNombreCompleto() == null || c.getNombreCompleto().trim().isEmpty()) {
            throw new ValidacionException("El nombre completo es obligatorio.");
        }
        if (c.getTipoLicencia() == null) {
            throw new ValidacionException("El tipo de licencia es obligatorio.");
        }
        if (c.getNumContacto() == null || c.getNumContacto().trim().isEmpty()) {
            throw new ValidacionException("El número de contacto es obligatorio.");
        }
    }
}
