package com.RapidExpress.controller;

import com.RapidExpress.model.entity.Auditoria;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.service.*;
import com.RapidExpress.view.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Report controller - coordinates operational reports and audit log views.
 * Connects directly to ReporteService, AuditoriaService, PaqueteService, RutaService, and ConductorService
 * to resolve Fallos 4 and 5 (SRP & DIP compliant).
 */
public class ReporteController {

    private final ReporteService reporteService;
    private final AuditoriaService auditoriaService;
    private final PaqueteService paqueteService;
    private final RutaService rutaService;
    private final ConductorService conductorService;

    public ReporteController() {
        this(new ReporteService(), new AuditoriaService(), new PaqueteService(), new RutaService(), new ConductorService());
    }

    public ReporteController(ReporteService reporteService, AuditoriaService auditoriaService,
                             PaqueteService paqueteService, RutaService rutaService, ConductorService conductorService) {
        this.reporteService = reporteService;
        this.auditoriaService = auditoriaService;
        this.paqueteService = paqueteService;
        this.rutaService = rutaService;
        this.conductorService = conductorService;
    }

    public void run() {
        MenuRunner.run(
                ReporteView::showMenu,
                ReporteView::readMenuOption,
                this::resumenFlota,                     // 1
                this::reporteConductores,               // 2
                this::reportePaquetesBodega,            // 3
                this::reporteRutasEnCurso,              // 4
                this::resumenCostosMantenimiento,       // 5
                this::reporteAuditoriasBD,              // 6
                this::reporteEntregasPorConductor,      // 7
                this::reporteHistorialRutasVehiculo,    // 8
                this::verArchivoAuditoria               // 9
        );
    }

    private void resumenFlota(int opt) {
        ConsoleUtil.printHeader("RESUMEN DE FLOTA Y ESTADO ACTUAL");
        String reporte = reporteService.generarResumenFlota();
        System.out.println(reporte);
        gestionarExportacion("resumen_flota.txt", reporte);
    }

    private void reporteConductores(int opt) {
        ConsoleUtil.printHeader("REPORTE DE CONDUCTORES");
        List<Conductor> conductores = conductorService.listarTodos();
        ConductorView.showConductorList(conductores);
        ConsoleUtil.printInfo("Total conductores registrados: " + conductores.size());
    }

    private void reportePaquetesBodega(int opt) {
        ConsoleUtil.printHeader("REPORTE DE PAQUETES EN BODEGA");
        List<Paquete> paquetes = paqueteService.listarEnBodega();
        PaqueteView.showPaqueteList(paquetes);
        ConsoleUtil.printInfo("Total paquetes en bodega: " + paquetes.size());
    }

    private void reporteRutasEnCurso(int opt) {
        ConsoleUtil.printHeader("REPORTE DE RUTAS EN CURSO");
        List<Ruta> rutas = rutaService.listarEnCurso();
        RutaView.showRutaList(rutas);
        ConsoleUtil.printInfo("Total rutas en curso: " + rutas.size());
    }

    private void resumenCostosMantenimiento(int opt) {
        ConsoleUtil.printHeader("REPORTE DE COSTOS DE MANTENIMIENTO POR FLOTA");
        String reporte = reporteService.generarResumenCostosMantenimiento();
        System.out.println(reporte);
        gestionarExportacion("costos_mantenimiento.txt", reporte);
    }

    private void reporteAuditoriasBD(int opt) {
        ConsoleUtil.printHeader("ULTIMAS AUDITORIAS REGISTRADAS EN BASE DE DATOS");
        List<Auditoria> auditorias = auditoriaService.listarUltimas(20);
        if (auditorias.isEmpty()) {
            ConsoleUtil.printInfo("No hay registros de auditoria en la base de datos.");
            return;
        }
        String[][] rows = new String[auditorias.size()][6];
        for (int i = 0; i < auditorias.size(); i++) {
            Auditoria a = auditorias.get(i);
            rows[i] = new String[]{
                    String.valueOf(a.getIdAuditoria()),
                    a.getTimestamp() != null ? a.getTimestamp().toString() : "",
                    a.getOperacion(),
                    a.getEntidad(),
                    a.getEntidadId(),
                    a.getUsuario() != null ? a.getUsuario() : "-"
            };
        }
        ConsoleUtil.printTable(new String[]{"ID", "Timestamp", "Operacion", "Entidad", "ID Entidad", "Usuario"}, rows);
    }

    /**
     * Fallo 5 Solucionado: Permite consultar entregas por conductor filtrando por rango de fechas.
     */
    private void reporteEntregasPorConductor(int opt) {
        ConsoleUtil.printHeader("REPORTE OPERATIVO: ENTREGAS POR CONDUCTOR");
        String conductorId = ReporteView.readConductorId("Ingrese la identificación del conductor: ");
        LocalDate inicio = ReporteView.readFechaInicio();
        LocalDate fin = ReporteView.readFechaFin();

        String reporte = reporteService.generarReporteEntregasPorConductor(conductorId, inicio, fin);
        System.out.println(reporte);
        gestionarExportacion("entregas_conductor_" + conductorId + ".txt", reporte);
    }

    /**
     * Fallo 5 Solucionado: Permite consultar el historial consolidado de rutas de un vehículo.
     */
    private void reporteHistorialRutasVehiculo(int opt) {
        ConsoleUtil.printHeader("REPORTE OPERATIVO: HISTORIAL DE RUTAS POR VEHICULO");
        String placa = ReporteView.readVehiculoPlaca("Ingrese la placa del vehículo: ");

        String reporte = reporteService.generarHistorialRutasVehiculo(placa);
        System.out.println(reporte);
        gestionarExportacion("historial_vehiculo_" + placa + ".txt", reporte);
    }

    /**
     * Fallo 4 Solucionado: Visualiza directamente las entradas del archivo plano centralizado logs/auditoria.log.
     */
    private void verArchivoAuditoria(int opt) {
        ConsoleUtil.printHeader("REGISTRO CENTRALIZADO DE AUDITORIA (ARCHIVO logs/auditoria.log)");
        List<String> lineas = auditoriaService.leerLogsDeArchivo();
        if (lineas.isEmpty()) {
            ConsoleUtil.printInfo("El archivo logs/auditoria.log aún no contiene registros o no ha sido creado.");
            return;
        }
        ConsoleUtil.printInfo("Total registros en archivo plano: " + lineas.size());
        ConsoleUtil.printLine();
        for (String linea : lineas) {
            System.out.println(linea);
        }
        ConsoleUtil.printLine();
    }

    private void gestionarExportacion(String defaultName, String contenido) {
        if (ReporteView.confirmExportar()) {
            String nombreArchivo = ReporteView.readNombreArchivo(defaultName);
            boolean ok = reporteService.exportarReporte(nombreArchivo, contenido);
            if (ok) {
                ConsoleUtil.printSuccess("Reporte exportado exitosamente a logs/" + nombreArchivo);
            } else {
                ConsoleUtil.printError("No se pudo exportar el reporte.");
            }
        }
    }
}