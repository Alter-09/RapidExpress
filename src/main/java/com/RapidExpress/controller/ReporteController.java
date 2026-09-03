package com.RapidExpress.controller;

import com.RapidExpress.dao.*;
import com.RapidExpress.model.entity.*;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.ReporteView;
import com.RapidExpress.view.VehiculoView;
import com.RapidExpress.view.ConductorView;
import com.RapidExpress.view.PaqueteView;
import com.RapidExpress.view.RutaView;
import com.RapidExpress.view.MantenimientoView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Report controller - read-only views across entities.
 * Uses MenuRunner to eliminate boilerplate menu loop.
 */
public class ReporteController {

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final ConductorDAO conductorDAO = new ConductorDAO();
    private final PaqueteDAO paqueteDAO = new PaqueteDAO();
    private final RutaDAO rutaDAO = new RutaDAO();
    private final MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public void run() {
        MenuRunner.run(
                ReporteView::showMenu,
                ReporteView::readMenuOption,
                this::reporteVehiculos,          // 1
                this::reporteConductores,        // 2
                this::reportePaquetesBodega,     // 3
                this::reporteRutasEnCurso,       // 4
                this::reporteMantenimientos,     // 5
                this::reporteAuditorias          // 6
        );
    }

    private void reporteVehiculos(int opt) {
        ConsoleUtil.printHeader("REPORTE DE VEHICULOS");
        List<Vehiculo> vehiculos = vehiculoDAO.findAll();
        VehiculoView.showVehiculoList(vehiculos);
        int total = vehiculoDAO.count();
        int disponibles = vehiculoDAO.countByEstado(com.RapidExpress.model.enums.EstadoVehiculo.DISPONIBLE);
        int enRuta = vehiculoDAO.countByEstado(com.RapidExpress.model.enums.EstadoVehiculo.EN_RUTA);
        int enMant = vehiculoDAO.countByEstado(com.RapidExpress.model.enums.EstadoVehiculo.EN_MANTENIMIENTO);
        ConsoleUtil.printLine();
        ConsoleUtil.printInfo("Total: " + total + " | Disponibles: " + disponibles +
                " | En ruta: " + enRuta + " | En mant.: " + enMant);
    }

    private void reporteConductores(int opt) {
        ConsoleUtil.printHeader("REPORTE DE CONDUCTORES");
        List<Conductor> conductores = conductorDAO.findAll();
        ConductorView.showConductorList(conductores);
        ConsoleUtil.printInfo("Total: " + conductorDAO.count());
    }

    private void reportePaquetesBodega(int opt) {
        ConsoleUtil.printHeader("REPORTE DE PAQUETES EN BODEGA");
        List<Paquete> paquetes = paqueteDAO.findEnBodega();
        PaqueteView.showPaqueteList(paquetes);
        ConsoleUtil.printInfo("Total en bodega: " + paquetes.size());
    }

    private void reporteRutasEnCurso(int opt) {
        ConsoleUtil.printHeader("REPORTE DE RUTAS EN CURSO");
        List<Ruta> rutas = rutaDAO.findEnCurso();
        RutaView.showRutaList(rutas);
        ConsoleUtil.printInfo("Rutas en curso: " + rutas.size());
    }

    private void reporteMantenimientos(int opt) {
        ConsoleUtil.printHeader("REPORTE DE MANTENIMIENTOS");
        List<Mantenimiento> mantenimientos = mantenimientoDAO.findAll();
        MantenimientoView.showMantenimientoList(mantenimientos);
        BigDecimal total = mantenimientos.stream()
                .map(Mantenimiento::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedio = mantenimientos.isEmpty() ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(mantenimientos.size()), 2, RoundingMode.HALF_UP);
        ConsoleUtil.printLine();
        ConsoleUtil.printInfo("Total mantenimientos: " + mantenimientos.size() +
                " | Costo total: $" + total + " | Promedio: $" + promedio);
    }

    private void reporteAuditorias(int opt) {
        ConsoleUtil.printHeader("ULTIMAS AUDITORIAS");
        List<Auditoria> auditorias = auditoriaDAO.findUltimas(20);
        if (auditorias.isEmpty()) { ConsoleUtil.printInfo("No hay registros de auditoria."); return; }
        String[][] rows = new String[auditorias.size()][6];
        for (int i = 0; i < auditorias.size(); i++) {
            Auditoria a = auditorias.get(i);
            rows[i] = new String[]{String.valueOf(a.getIdAuditoria()),
                    a.getTimestamp() != null ? a.getTimestamp().toString() : "",
                    a.getOperacion(), a.getEntidad(), a.getEntidadId(),
                    a.getUsuario() != null ? a.getUsuario() : "-"};
        }
        ConsoleUtil.printTable(new String[]{"ID", "Timestamp", "Operacion", "Entidad", "ID", "Usuario"}, rows);
    }
}