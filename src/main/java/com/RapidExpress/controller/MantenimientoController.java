package com.RapidExpress.controller;

import com.RapidExpress.dao.MantenimientoDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.TipoMantenimiento;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.MantenimientoView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Maintenance controller - orchestrates maintenance registration, filtering, and cost stats.
 * Uses MenuRunner to eliminate boilerplate menu loop.
 */
public class MantenimientoController {

    private final MantenimientoDAO dao = new MantenimientoDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();

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
        List<Vehiculo> vehiculos = vehiculoDAO.findAll();
        Mantenimiento mant = MantenimientoView.readNewMantenimientoData(vehiculos);
        if (mant != null) {
            if (dao.create(mant)) {
                ConsoleUtil.printSuccess("Mantenimiento registrado exitosamente. ID: " + mant.getIdMantenimiento());

                // Actualizar estado del vehiculo si es mantenimiento correctivo o preventivo
                if (mant.getTipo() == TipoMantenimiento.CORRECTIVO || mant.getTipo() == TipoMantenimiento.PREVENTIVO) {
                    vehiculoDAO.updateEstado(mant.getVehiculoPlaca(), com.RapidExpress.model.enums.EstadoVehiculo.EN_MANTENIMIENTO);
                    ConsoleUtil.printInfo("Estado del vehiculo actualizado a EN_MANTENIMIENTO");
                }
            } else {
                ConsoleUtil.printError("No se pudo registrar el mantenimiento.");
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE MANTENIMIENTOS");
        List<Mantenimiento> mantenimientos = dao.findAll();
        MantenimientoView.showMantenimientoList(mantenimientos);
    }

    private void verPorVehiculo(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS POR VEHICULO");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo: ").toUpperCase();
        List<Mantenimiento> mantenimientos = dao.findByVehiculo(placa);
        MantenimientoView.showMantenimientosByVehiculo(mantenimientos, placa);
    }

    private void verPorFecha(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS POR FECHA");
        LocalDate fecha = MantenimientoView.readFecha("Fecha");
        List<Mantenimiento> mantenimientos = dao.findByFecha(fecha);
        MantenimientoView.showMantenimientosByFecha(mantenimientos, fecha);
    }

    private void verPorTipo(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS POR TIPO");
        TipoMantenimiento tipo = MantenimientoView.readTipo();
        List<Mantenimiento> mantenimientos = dao.findByTipo(tipo);
        MantenimientoView.showMantenimientosByTipo(mantenimientos, tipo);
    }

    private void verHistorialVehiculo(int opt) {
        ConsoleUtil.printSubHeader("HISTORIAL DE MANTENIMIENTO DE VEHICULO");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo: ").toUpperCase();
        List<Mantenimiento> mantenimientos = dao.findByVehiculo(placa);
        Vehiculo vehiculo = vehiculoDAO.findById(placa).orElse(null);
        if (vehiculo != null) {
            MantenimientoView.showHistorialVehiculo(mantenimientos, vehiculo);
        } else {
            ConsoleUtil.printError("Vehiculo no encontrado.");
        }
    }

    private void verRecientes(int opt) {
        ConsoleUtil.printSubHeader("MANTENIMIENTOS ULTIMOS 6 MESES");
        LocalDate hace6Meses = LocalDate.now().minusMonths(6);
        List<Mantenimiento> mantenimientos = dao.findByRangoFechas(hace6Meses, LocalDate.now());
        MantenimientoView.showMantenimientosRecientes(mantenimientos);
    }

    private void verEstadisticas(int opt) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE COSTOS");
        List<Mantenimiento> todos = dao.findAll();

        BigDecimal total = todos.stream()
                .map(Mantenimiento::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int count = todos.size();
        BigDecimal promedio = count > 0
                ? total.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int preventivos = (int) todos.stream().filter(m -> m.getTipo() == TipoMantenimiento.PREVENTIVO).count();
        int correctivos  = (int) todos.stream().filter(m -> m.getTipo() == TipoMantenimiento.CORRECTIVO).count();
        int inspecciones = (int) todos.stream().filter(m -> m.getTipo() == TipoMantenimiento.INSPECCION).count();

        MantenimientoView.showEstadisticasCostos(total, count, promedio, preventivos, correctivos, inspecciones);
    }
}
