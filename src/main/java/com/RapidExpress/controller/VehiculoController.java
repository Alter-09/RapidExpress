package com.RapidExpress.controller;

import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.VehiculoView;

import java.util.List;

/**
 * Vehicle controller - orchestrates vehicle CRUD and state management.
 * Uses MenuRunner to eliminate boilerplate menu loop.
 */
public class VehiculoController {

    private final VehiculoDAO dao = new VehiculoDAO();

    public void run() {
        MenuRunner.run(
                VehiculoView::showMenu,
                VehiculoView::readMenuOption,
                this::crearVehiculo,        // 1
                this::listarTodos,          // 2
                this::buscarPorPlaca,       // 3
                this::actualizarVehiculo,   // 4
                this::cambiarEstado,        // 5
                this::eliminarVehiculo,     // 6
                this::verPorEstado,         // 7
                this::verEstadisticas       // 8
        );
    }

    private void crearVehiculo(int opt) {
        ConsoleUtil.printSubHeader("REGISTRAR NUEVO VEHICULO");
        Vehiculo vehiculo = VehiculoView.readNewVehiculoData();
        if (vehiculo != null) {
            if (dao.create(vehiculo)) {
                ConsoleUtil.printSuccess("Vehiculo registrado exitosamente.");
            } else {
                ConsoleUtil.printError("No se pudo registrar el vehiculo.");
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE VEHICULOS");
        List<Vehiculo> vehiculos = dao.findAll();
        VehiculoView.showVehiculoList(vehiculos);
    }

    private void buscarPorPlaca(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR VEHICULO POR PLACA");
        String placa = VehiculoView.readPlaca("Ingrese la placa");
        dao.findById(placa).ifPresentOrElse(
                VehiculoView::showVehiculoDetail,
                () -> ConsoleUtil.printError("Vehiculo no encontrado.")
        );
    }

    private void actualizarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR VEHICULO");
        String placa = VehiculoView.readPlaca("Ingrese la placa del vehiculo a actualizar");
        dao.findById(placa).ifPresentOrElse(vehiculo -> {
            Vehiculo actualizado = VehiculoView.readUpdatedVehiculoData(vehiculo);
            if (dao.update(actualizado)) {
                ConsoleUtil.printSuccess("Vehiculo actualizado exitosamente.");
            } else {
                ConsoleUtil.printError("No se pudo actualizar el vehiculo.");
            }
        }, () -> ConsoleUtil.printError("Vehiculo no encontrado."));
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DEL VEHICULO");
        String placa = VehiculoView.readPlaca("Ingrese la placa");
        dao.findById(placa).ifPresentOrElse(vehiculo -> {
            VehiculoView.showVehiculoDetail(vehiculo);
            EstadoVehiculo nuevoEstado = VehiculoView.readNewEstado();
            if (dao.updateEstado(placa, nuevoEstado)) {
                ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
            } else {
                ConsoleUtil.printError("No se pudo actualizar el estado.");
            }
        }, () -> ConsoleUtil.printError("Vehiculo no encontrado."));
    }

    private void eliminarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("ELIMINAR VEHICULO");
        String placa = VehiculoView.readPlaca("Ingrese la placa del vehiculo a eliminar");
        dao.findById(placa).ifPresentOrElse(vehiculo -> {
            VehiculoView.showVehiculoDetail(vehiculo);
            if (VehiculoView.confirmDelete(placa)) {
                if (dao.delete(placa)) {
                    ConsoleUtil.printSuccess("Vehiculo eliminado exitosamente.");
                } else {
                    ConsoleUtil.printError("No se pudo eliminar el vehiculo.");
                }
            } else {
                ConsoleUtil.printInfo("Operacion cancelada.");
            }
        }, () -> ConsoleUtil.printError("Vehiculo no encontrado."));
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("VEHICULOS POR ESTADO");
        EstadoVehiculo estado = VehiculoView.readNewEstado();
        List<Vehiculo> vehiculos = dao.findByEstado(estado);
        VehiculoView.showVehiculosByEstado(vehiculos, estado);
    }

    private void verEstadisticas(int opt) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE FLOTA");
        int total = dao.count();
        int disponibles = dao.countByEstado(EstadoVehiculo.DISPONIBLE);
        int enRuta = dao.countByEstado(EstadoVehiculo.EN_RUTA);
        int enMantenimiento = dao.countByEstado(EstadoVehiculo.EN_MANTENIMIENTO);
        VehiculoView.showStats(total, disponibles, enRuta, enMantenimiento);
    }
}
