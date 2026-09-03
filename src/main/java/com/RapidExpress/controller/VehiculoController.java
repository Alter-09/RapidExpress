package com.RapidExpress.controller;

import com.RapidExpress.exception.RapidExpressException;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;
import com.RapidExpress.service.VehiculoService;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.VehiculoView;

import java.util.List;

/**
 * Vehicle controller - orchestrates vehicle CRUD and state management.
 * Connects directly to VehiculoService to guarantee validation, business invariants,
 * and centralized audit logging (Fixes Fallo 4, complies with SRP/DIP).
 */
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private static final String DEFAULT_USER = "OPERADOR_CLI";

    public VehiculoController() {
        this(new VehiculoService());
    }

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

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
            try {
                vehiculoService.registrarVehiculo(vehiculo, DEFAULT_USER);
                ConsoleUtil.printSuccess("Vehiculo registrado exitosamente con placa: " + vehiculo.getPlaca());
            } catch (RapidExpressException e) {
                ConsoleUtil.printError("Error al registrar vehículo: " + e.getMessage());
            } catch (Exception e) {
                ConsoleUtil.printError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE VEHICULOS");
        List<Vehiculo> vehiculos = vehiculoService.listarTodos();
        VehiculoView.showVehiculoList(vehiculos);
    }

    private void buscarPorPlaca(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR VEHICULO POR PLACA");
        String placa = VehiculoView.readPlaca("Ingrese la placa");
        try {
            Vehiculo vehiculo = vehiculoService.obtenerVehiculo(placa);
            VehiculoView.showVehiculoDetail(vehiculo);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void actualizarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR VEHICULO");
        String placa = VehiculoView.readPlaca("Ingrese la placa del vehiculo a actualizar");
        try {
            Vehiculo vehiculo = vehiculoService.obtenerVehiculo(placa);
            VehiculoView.showVehiculoDetail(vehiculo);
            Vehiculo actualizado = VehiculoView.readUpdatedVehiculoData(vehiculo);
            vehiculoService.actualizarVehiculo(actualizado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Vehiculo actualizado exitosamente.");
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al actualizar vehículo: " + e.getMessage());
        }
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DEL VEHICULO");
        String placa = VehiculoView.readPlaca("Ingrese la placa");
        try {
            Vehiculo vehiculo = vehiculoService.obtenerVehiculo(placa);
            VehiculoView.showVehiculoDetail(vehiculo);
            EstadoVehiculo nuevoEstado = VehiculoView.readNewEstado();
            vehiculoService.cambiarEstado(placa, nuevoEstado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al cambiar estado: " + e.getMessage());
        }
    }

    private void eliminarVehiculo(int opt) {
        ConsoleUtil.printSubHeader("ELIMINAR VEHICULO");
        String placa = VehiculoView.readPlaca("Ingrese la placa del vehiculo a eliminar");
        try {
            Vehiculo vehiculo = vehiculoService.obtenerVehiculo(placa);
            VehiculoView.showVehiculoDetail(vehiculo);
            if (VehiculoView.confirmDelete(placa)) {
                vehiculoService.eliminarVehiculo(placa, DEFAULT_USER);
                ConsoleUtil.printSuccess("Vehiculo eliminado exitosamente.");
            } else {
                ConsoleUtil.printInfo("Operacion cancelada.");
            }
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al eliminar vehículo: " + e.getMessage());
        }
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("VEHICULOS POR ESTADO");
        EstadoVehiculo estado = VehiculoView.readNewEstado();
        List<Vehiculo> vehiculos = vehiculoService.listarPorEstado(estado);
        VehiculoView.showVehiculosByEstado(vehiculos, estado);
    }

    private void verEstadisticas(int opt) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE FLOTA");
        int total = vehiculoService.contarVehiculos();
        int disponibles = vehiculoService.contarPorEstado(EstadoVehiculo.DISPONIBLE);
        int enRuta = vehiculoService.contarPorEstado(EstadoVehiculo.EN_RUTA);
        int enMantenimiento = vehiculoService.contarPorEstado(EstadoVehiculo.EN_MANTENIMIENTO);
        VehiculoView.showStats(total, disponibles, enRuta, enMantenimiento);
    }
}
