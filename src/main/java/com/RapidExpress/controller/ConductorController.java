package com.RapidExpress.controller;

import com.RapidExpress.exception.RapidExpressException;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.TipoLicencia;
import com.RapidExpress.service.ConductorService;
import com.RapidExpress.service.VehiculoService;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.ConductorView;
import com.RapidExpress.view.VehiculoView;

import java.util.List;

/**
 * Driver controller - orchestrates driver CRUD, vehicle assignment/release, and filtering.
 * Connects directly to ConductorService and VehiculoService (Solves Fallo 4, complies with SRP/DIP).
 */
public class ConductorController {

    private final ConductorService conductorService;
    private final VehiculoService vehiculoService;
    private static final String DEFAULT_USER = "OPERADOR_CLI";

    public ConductorController() {
        this(new ConductorService(), new VehiculoService());
    }

    public ConductorController(ConductorService conductorService, VehiculoService vehiculoService) {
        this.conductorService = conductorService;
        this.vehiculoService = vehiculoService;
    }

    public void run() {
        MenuRunner.run(
                ConductorView::showMenu,
                ConductorView::readMenuOption,
                this::crearConductor,           // 1
                this::listarTodos,              // 2
                this::buscarPorIdentificacion,  // 3
                this::actualizarConductor,      // 4
                this::cambiarEstado,            // 5
                this::asignarVehiculo,          // 6
                this::liberarVehiculo,          // 7
                this::eliminarConductor,        // 8
                this::verPorEstado,             // 9
                this::verDisponibles,           // 10
                this::verPorTipoLicencia        // 11
        );
    }

    private void crearConductor(int opt) {
        ConsoleUtil.printSubHeader("REGISTRAR NUEVO CONDUCTOR");
        Conductor conductor = ConductorView.readNewConductorData();
        if (conductor != null) {
            try {
                conductorService.registrarConductor(conductor, DEFAULT_USER);
                ConsoleUtil.printSuccess("Conductor registrado exitosamente con ID: " + conductor.getNumIdentificacion());
            } catch (RapidExpressException e) {
                ConsoleUtil.printError("Error al registrar conductor: " + e.getMessage());
            } catch (Exception e) {
                ConsoleUtil.printError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE CONDUCTORES");
        List<Conductor> conductores = conductorService.listarTodos();
        ConductorView.showConductorList(conductores);
    }

    private void buscarPorIdentificacion(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR CONDUCTOR POR IDENTIFICACION");
        String id = ConductorView.readIdentificacion("Ingrese el numero de identificacion");
        try {
            Conductor conductor = conductorService.obtenerConductor(id);
            ConductorView.showConductorDetail(conductor);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void actualizarConductor(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor a actualizar");
        try {
            Conductor conductor = conductorService.obtenerConductor(id);
            ConductorView.showConductorDetail(conductor);
            Conductor actualizado = ConductorView.readUpdatedConductorData(conductor);
            conductorService.actualizarConductor(actualizado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Conductor actualizado exitosamente.");
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al actualizar conductor: " + e.getMessage());
        }
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DEL CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion");
        try {
            Conductor conductor = conductorService.obtenerConductor(id);
            ConductorView.showConductorDetail(conductor);
            EstadoConductor nuevoEstado = ConductorView.readNewEstado();
            conductorService.cambiarEstado(id, nuevoEstado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al cambiar estado: " + e.getMessage());
        }
    }

    private void asignarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("ASIGNAR VEHICULO A CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor");
        try {
            Conductor conductor = conductorService.obtenerConductor(id);
            ConductorView.showConductorDetail(conductor);
            List<Vehiculo> disponibles = vehiculoService.listarDisponibles();
            if (disponibles.isEmpty()) {
                ConsoleUtil.printError("No hay vehiculos disponibles para asignar.");
                return;
            }
            VehiculoView.showVehiculoList(disponibles);
            String placa = ConductorView.readVehiculoPlaca();
            conductorService.asignarVehiculo(id, placa, DEFAULT_USER);
            ConductorView.showAsignacionInfo(id, placa);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al asignar vehículo: " + e.getMessage());
        }
    }

    private void liberarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("LIBERAR VEHICULO DE CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor");
        try {
            Conductor conductor = conductorService.obtenerConductor(id);
            if (conductor.getVehiculoAsignado() == null) {
                ConsoleUtil.printInfo("El conductor no tiene vehiculo asignado.");
                return;
            }
            ConductorView.showConductorDetail(conductor);
            if (ConsoleUtil.readConfirmation("¿Liberar vehículo " + conductor.getVehiculoAsignado() + "?")) {
                conductorService.liberarVehiculo(id, DEFAULT_USER);
                ConductorView.showLiberacionInfo(id);
            }
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al liberar vehículo: " + e.getMessage());
        }
    }

    private void eliminarConductor(int opt) {
        ConsoleUtil.printSubHeader("ELIMINAR CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor a eliminar");
        try {
            Conductor conductor = conductorService.obtenerConductor(id);
            ConductorView.showConductorDetail(conductor);
            if (ConductorView.confirmDelete(id)) {
                conductorService.eliminarConductor(id, DEFAULT_USER);
                ConsoleUtil.printSuccess("Conductor eliminado exitosamente.");
            } else {
                ConsoleUtil.printInfo("Operacion cancelada.");
            }
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al eliminar conductor: " + e.getMessage());
        }
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("CONDUCTORES POR ESTADO");
        EstadoConductor estado = ConductorView.readNewEstado();
        List<Conductor> conductores = conductorService.listarPorEstado(estado);
        ConductorView.showConductoresByEstado(conductores, estado);
    }

    private void verDisponibles(int opt) {
        ConsoleUtil.printSubHeader("CONDUCTORES DISPONIBLES");
        List<Conductor> conductores = conductorService.listarDisponibles();
        ConductorView.showConductoresDisponibles(conductores);
    }

    private void verPorTipoLicencia(int opt) {
        ConsoleUtil.printSubHeader("CONDUCTORES POR TIPO DE LICENCIA");
        TipoLicencia tipo = ConductorView.readTipoLicencia();
        List<Conductor> conductores = conductorService.listarPorTipoLicencia(tipo);
        ConductorView.showConductoresByTipoLicencia(conductores, tipo);
    }
}
