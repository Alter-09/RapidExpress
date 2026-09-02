package com.RapidExpress.service;

import com.RapidExpress.dao.MantenimientoDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.exception.EntidadNoEncontradaException;
import com.RapidExpress.exception.EstadoInvalidoException;
import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Capa de servicios y lógica de negocio para la programación y control del historial de mantenimientos.
 */
public class MantenimientoService {

    private final MantenimientoDAO mantenimientoDAO;
    private final VehiculoDAO vehiculoDAO;
    private final AuditoriaService auditoriaService;

    public MantenimientoService() {
        this.mantenimientoDAO = new MantenimientoDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.auditoriaService = new AuditoriaService();
    }

    public MantenimientoService(MantenimientoDAO mantenimientoDAO, VehiculoDAO vehiculoDAO, AuditoriaService auditoriaService) {
        this.mantenimientoDAO = mantenimientoDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.auditoriaService = auditoriaService;
    }

    public Long programarMantenimiento(Mantenimiento mantenimiento, boolean cambiarEstadoVehiculo, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {

        validarMantenimiento(mantenimiento);

        Vehiculo vehiculo = vehiculoDAO.findById(mantenimiento.getVehiculoPlaca())
                .orElseThrow(() -> new EntidadNoEncontradaException("Vehículo", mantenimiento.getVehiculoPlaca()));

        if (vehiculo.isEnRuta()) {
            throw new EstadoInvalidoException("No se puede programar mantenimiento para un vehículo que está En Ruta.");
        }

        boolean creado = mantenimientoDAO.create(mantenimiento);
        if (!creado || mantenimiento.getIdMantenimiento() == null) {
            throw new ValidacionException("No se pudo registrar el mantenimiento.");
        }

        if (cambiarEstadoVehiculo) {
            vehiculoDAO.updateEstado(vehiculo.getPlaca(), EstadoVehiculo.EN_MANTENIMIENTO);
        }

        auditoriaService.registrar("CREATE_MAINTENANCE", "mantenimiento",
                String.valueOf(mantenimiento.getIdMantenimiento()), usuario,
                String.format("Vehículo: %s, Tipo: %s, Costo: %.2f, Proveedor: %s",
                        mantenimiento.getVehiculoPlaca(), mantenimiento.getTipo(),
                        mantenimiento.getCosto(), mantenimiento.getProveedor()));

        return mantenimiento.getIdMantenimiento();
    }

    public void finalizarMantenimiento(Long idMantenimiento, String usuario)
            throws EntidadNoEncontradaException, ValidacionException {

        Mantenimiento mantenimiento = obtenerMantenimiento(idMantenimiento);
        Vehiculo vehiculo = vehiculoDAO.findById(mantenimiento.getVehiculoPlaca())
                .orElseThrow(() -> new EntidadNoEncontradaException("Vehículo", mantenimiento.getVehiculoPlaca()));

        if (vehiculo.isEnMantenimiento()) {
            vehiculoDAO.updateEstado(vehiculo.getPlaca(), EstadoVehiculo.DISPONIBLE);
        }

        auditoriaService.registrar("FINISH_MAINTENANCE", "mantenimiento",
                String.valueOf(idMantenimiento), usuario,
                String.format("Mantenimiento ID %d finalizado. Vehículo %s restaurado a Disponible.",
                        idMantenimiento, vehiculo.getPlaca()));
    }

    public void actualizarMantenimiento(Mantenimiento mantenimiento, String usuario)
            throws EntidadNoEncontradaException, ValidacionException {

        validarMantenimiento(mantenimiento);
        obtenerMantenimiento(mantenimiento.getIdMantenimiento());

        boolean actualizado = mantenimientoDAO.update(mantenimiento);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el registro de mantenimiento.");
        }

        auditoriaService.registrar("UPDATE", "mantenimiento",
                String.valueOf(mantenimiento.getIdMantenimiento()), usuario,
                String.format("Mantenimiento ID %d actualizado.", mantenimiento.getIdMantenimiento()));
    }

    public void eliminarMantenimiento(Long idMantenimiento, String usuario)
            throws EntidadNoEncontradaException, ValidacionException {

        obtenerMantenimiento(idMantenimiento);

        boolean eliminado = mantenimientoDAO.delete(idMantenimiento);
        if (!eliminado) {
            throw new ValidacionException("No se pudo eliminar el mantenimiento.");
        }

        auditoriaService.registrar("DELETE", "mantenimiento", String.valueOf(idMantenimiento), usuario, "Mantenimiento eliminado.");
    }

    public Mantenimiento obtenerMantenimiento(Long idMantenimiento) throws EntidadNoEncontradaException {
        if (idMantenimiento == null || idMantenimiento <= 0) {
            throw new IllegalArgumentException("El ID de mantenimiento debe ser válido.");
        }
        return mantenimientoDAO.findById(idMantenimiento)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mantenimiento", String.valueOf(idMantenimiento)));
    }

    public List<Mantenimiento> listarTodos() {
        return mantenimientoDAO.findAll();
    }

    public List<Mantenimiento> listarPorVehiculo(String placa) {
        return mantenimientoDAO.findByVehiculo(placa);
    }

    public List<Mantenimiento> listarPorRangoFechas(LocalDate inicio, LocalDate fin) {
        return mantenimientoDAO.findByRangoFechas(inicio, fin);
    }

    public BigDecimal calcularCostoTotalPorVehiculo(String placa) {
        List<Mantenimiento> lista = mantenimientoDAO.findByVehiculo(placa);
        BigDecimal total = BigDecimal.ZERO;
        for (Mantenimiento m : lista) {
            if (m.getCosto() != null) {
                total = total.add(m.getCosto());
            }
        }
        return total;
    }

    private void validarMantenimiento(Mantenimiento m) throws ValidacionException {
        if (m == null) {
            throw new ValidacionException("El mantenimiento no puede ser nulo.");
        }
        if (m.getVehiculoPlaca() == null || m.getVehiculoPlaca().trim().isEmpty()) {
            throw new ValidacionException("La placa del vehículo es obligatoria.");
        }
        if (m.getFecha() == null) {
            throw new ValidacionException("La fecha del mantenimiento es obligatoria.");
        }
        if (m.getTipo() == null) {
            throw new ValidacionException("El tipo de mantenimiento es obligatorio.");
        }
        if (m.getDescripcion() == null || m.getDescripcion().trim().isEmpty()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
        if (m.getCosto() == null || m.getCosto().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("El costo no puede ser negativo.");
        }
    }
}
