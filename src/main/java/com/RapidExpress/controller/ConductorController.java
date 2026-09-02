package com.RapidExpress.controller;

import com.RapidExpress.dao.ConductorDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.TipoLicencia;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.ConductorView;
import com.RapidExpress.view.VehiculoView;

import java.util.List;

/**
 * Driver controller - orchestrates driver CRUD, vehicle assignment/release, and filtering.
 * Uses MenuRunner to eliminate boilerplate menu loop.
 */
public class ConductorController {

    private final ConductorDAO dao = new ConductorDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();

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
            if (dao.create(conductor)) {
                ConsoleUtil.printSuccess("Conductor registrado exitosamente.");
            } else {
                ConsoleUtil.printError("No se pudo registrar el conductor.");
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE CONDUCTORES");
        List<Conductor> conductores = dao.findAll();
        ConductorView.showConductorList(conductores);
    }

    private void buscarPorIdentificacion(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR CONDUCTOR POR IDENTIFICACION");
        String id = ConductorView.readIdentificacion("Ingrese el numero de identificacion");
        dao.findById(id).ifPresentOrElse(
                ConductorView::showConductorDetail,
                () -> ConsoleUtil.printError("Conductor no encontrado.")
        );
    }

    private void actualizarConductor(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor a actualizar");
        dao.findById(id).ifPresentOrElse(conductor -> {
            Conductor actualizado = ConductorView.readUpdatedConductorData(conductor);
            if (dao.update(actualizado)) {
                ConsoleUtil.printSuccess("Conductor actualizado exitosamente.");
            } else {
                ConsoleUtil.printError("No se pudo actualizar el conductor.");
            }
        }, () -> ConsoleUtil.printError("Conductor no encontrado."));
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DEL CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion");
        dao.findById(id).ifPresentOrElse(conductor -> {
            ConductorView.showConductorDetail(conductor);
            EstadoConductor nuevoEstado = ConductorView.readNewEstado();
            if (dao.updateEstado(id, nuevoEstado)) {
                ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
            } else {
                ConsoleUtil.printError("No se pudo actualizar el estado.");
            }
        }, () -> ConsoleUtil.printError("Conductor no encontrado."));
    }

    private void asignarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("ASIGNAR VEHICULO A CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor");
        dao.findById(id).ifPresentOrElse(conductor -> {
            ConductorView.showConductorDetail(conductor);
            List<Vehiculo> disponibles = vehiculoDAO.findDisponibles();
            if (disponibles.isEmpty()) {
                ConsoleUtil.printError("No hay vehiculos disponibles para asignar.");
                return;
            }
            VehiculoView.showVehiculoList(disponibles);
            String placa = ConductorView.readVehiculoPlaca();
            if (dao.asignarVehiculo(id, placa)) {
                ConductorView.showAsignacionInfo(id, placa);
            } else {
                ConsoleUtil.printError("No se pudo asignar el vehiculo.");
            }
        }, () -> ConsoleUtil.printError("Conductor no encontrado."));
    }

    private void liberarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("LIBERAR VEHICULO DE CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor");
        dao.findById(id).ifPresentOrElse(conductor -> {
            if (conductor.getVehiculoAsignado() == null) {
                ConsoleUtil.printInfo("El conductor no tiene vehiculo asignado.");
                return;
            }
            ConductorView.showConductorDetail(conductor);
            if (ConsoleUtil.readConfirmation("Liberar vehiculo " + conductor.getVehiculoAsignado() + "?")) {
                if (dao.liberarVehiculo(id)) {
                    ConductorView.showLiberacionInfo(id);
                } else {
                    ConsoleUtil.printError("No se pudo liberar el vehiculo.");
                }
            }
        }, () -> ConsoleUtil.printError("Conductor no encontrado."));
    }

    private void eliminarConductor(int opt) {
        ConsoleUtil.printSubHeader("ELIMINAR CONDUCTOR");
        String id = ConductorView.readIdentificacion("Ingrese la identificacion del conductor a eliminar");
        dao.findById(id).ifPresentOrElse(conductor -> {
            ConductorView.showConductorDetail(conductor);
            if (ConductorView.confirmDelete(id)) {
                if (dao.delete(id)) {
                    ConsoleUtil.printSuccess("Conductor eliminado exitosamente.");
                } else {
                    ConsoleUtil.printError("No se pudo eliminar el conductor.");
                }
            } else {
                ConsoleUtil.printInfo("Operacion cancelada.");
            }
        }, () -> ConsoleUtil.printError("Conductor no encontrado."));
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("CONDUCTORES POR ESTADO");
        EstadoConductor estado = ConductorView.readNewEstado();
        List<Conductor> conductores = dao.findByEstado(estado);
        ConductorView.showConductoresByEstado(conductores, estado);
    }

    private void verDisponibles(int opt) {
        ConsoleUtil.printSubHeader("CONDUCTORES DISPONIBLES");
        List<Conductor> conductores = dao.findDisponibles();
        ConductorView.showConductoresDisponibles(conductores);
    }

    private void verPorTipoLicencia(int opt) {
        ConsoleUtil.printSubHeader("CONDUCTORES POR TIPO DE LICENCIA");
        TipoLicencia tipo = ConsoleUtil.readEnum(TipoLicencia.class, "Seleccione el tipo de licencia:");
        List<Conductor> conductores = dao.findByTipoLicencia(tipo);
        ConductorView.showConductoresByTipoLicencia(conductores, tipo);
    }
}
