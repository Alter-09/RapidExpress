package com.RapidExpress.controller;

import com.RapidExpress.exception.RapidExpressException;
import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.TipoMantenimiento;
import com.RapidExpress.service.MantenimientoService;
import com.RapidExpress.service.VehiculoService;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.MantenimientoView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Maintenance controller - orchestrates maintenance registration, filtering, and cost stats.
 * Connects directly to MantenimientoService and VehiculoService (Solves Fallo 4, complies with SRP/DIP).
 */
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;
    private final VehiculoService vehiculoService;
    private static final String DEFAULT_USER = "OPERADOR_CLI";

    public MantenimientoController() {
        this(new MantenimientoService(), new VehiculoService());
    }

    public MantenimientoController(MantenimientoService mantenimientoService, VehiculoService vehiculoService) {
        this.mantenimientoService = mantenimientoService;
        this.vehiculoService = vehiculoService;
    }

    public void run() {
        MenuRunner.run(
                MantenimientoView::showMenu,
                MantenimientoView::readMenuOption,
                this::crearMantenimiento,       // 1
                this::listarTodos,              // 2
                this::verPorVehiculo,           // 3
                this::verPorFecha,              // 4
                this::verPorTipo,               // 5
                this::verHistorialVehiculo,     // 6
                this::verRecientes,             // 7
                this::verEstadisticas           // 8
        );
    }

    private void crearMantenimiento(int opt) {
        ConsoleUtil.printSubHeader("REGISTRAR NUEVO MANTENIMIENTO");
        List<Vehiculo> vehiculos = vehiculoService.listarTodos();
        Mantenimiento mant = MantenimientoView.readNewMantenimientoData(vehiculos);
        if (mant != null) {
            boolean cambiarEstado = (mant.getTipo() == TipoMantenimiento.CORRECTIVO || mant.getTipo() == TipoMantenimiento.PREVENTIVO);
            try {
                Long id = mantenimientoService.programarMantenimiento(mant, cambiarEstado, DEFAULT_USER);
                ConsoleUtil.printSuccess("Mantenimiento registrado exitosamente. ID: " + id);
                if (cambiarEstado) {
                    ConsoleUtil.printInfo("Estado del vehículo actualizado a EN_MANTENIMIENTO");
                }
            } catch (RapidExpressException e) {
                ConsoleUtil.printError("Error al registrar mantenimiento: " + e.getMessage());
            } catch (Exception e) {
                ConsoleUtil.printError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE MANTENIMIENTOS");
        List<Mantenimiento> mantenimientos = mantenimientoService.listarTodos();
        MantenimientoView.showMantenimientoList(mantenimientos);
    }

    private void verPorVehiculo(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS POR VEHICULO");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo: ").toUpperCase();
        List<Mantenimiento> mantenimientos = mantenimientoService.listarPorVehiculo(placa);
        MantenimientoView.showMantenimientosByVehiculo(mantenimientos, placa);
    }

    private void verPorFecha(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS POR FECHA");
        LocalDate fecha = MantenimientoView.readFecha("Fecha");
        List<Mantenimiento> mantenimientos = mantenimientoService.listarPorFecha(fecha);
        MantenimientoView.showMantenimientosByFecha(mantenimientos, fecha);
    }

    private void verPorTipo(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS POR TIPO");
        TipoMantenimiento tipo = MantenimientoView.readTipo();
        List<Mantenimiento> mantenimientos = mantenimientoService.listarPorTipo(tipo);
        MantenimientoView.showMantenimientosByTipo(mantenimientos, tipo);
    }

    private void verHistorialVehiculo(int opt) {
        ConsoleUtil.printSubHeader("HISTORIAL DE MANTENIMIENTO DE VEHICULO");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo: ").toUpperCase();
        List<Mantenimiento> mantenimientos = mantenimientoService.listarPorVehiculo(placa);
        try {
            Vehiculo vehiculo = vehiculoService.obtenerVehiculo(placa);
            MantenimientoView.showHistorialVehiculo(mantenimientos, vehiculo);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Vehiculo no encontrado: " + e.getMessage());
        }
    }

    private void verRecientes(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS ULTIMOS 6 MESES");
        LocalDate hace6Meses = LocalDate.now().minusMonths(6);
        List<Mantenimiento> mantenimientos = mantenimientoService.listarPorRangoFechas(hace6Meses, LocalDate.now());
        MantenimientoView.showMantenimientosRecientes(mantenimientos);
    }

    private void verEstadisticas(int opt) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE COSTOS");
        List<Mantenimiento> todos = mantenimientoService.listarTodos();

        BigDecimal total = todos.stream()
                .map(Mantenimiento::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int count = todos.size();
        BigDecimal promedio = count > 0
                ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int preventivos = (int) todos.stream().filter(m -> m.getTipo() == TipoMantenimiento.PREVENTIVO).count();
        int correctivos  = (int) todos.stream().filter(m -> m.getTipo() == TipoMantenimiento.CORRECTIVO).count();
        int inspecciones = (int) todos.stream().filter(m -> m.getTipo() == TipoMantenimiento.INSPECCION).count();

        MantenimientoView.showEstadisticasCostos(total, count, promedio, preventivos, correctivos, inspecciones);
    }
}
