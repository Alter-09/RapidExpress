package com.RapidExpress.service;

import com.RapidExpress.dao.ConductorDAO;
import com.RapidExpress.dao.MantenimientoDAO;
import com.RapidExpress.dao.PaqueteDAO;
import com.RapidExpress.dao.RutaDAO;
import com.RapidExpress.dao.RutaPaqueteDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.entity.RutaPaquete;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoRutaPaquete;
import com.RapidExpress.util.FileLogUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para generación, consolidación y exportación de reportes operativos.
 */
public class ReporteService {

    private final RutaDAO rutaDAO;
    private final RutaPaqueteDAO rutaPaqueteDAO;
    private final VehiculoDAO vehiculoDAO;
    private final ConductorDAO conductorDAO;
    private final PaqueteDAO paqueteDAO;
    private final MantenimientoDAO mantenimientoDAO;

    public ReporteService() {
        this.rutaDAO = new RutaDAO();
        this.rutaPaqueteDAO = new RutaPaqueteDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.conductorDAO = new ConductorDAO();
        this.paqueteDAO = new PaqueteDAO();
        this.mantenimientoDAO = new MantenimientoDAO();
    }

    /**
     * Genera el reporte de entregas realizadas por un conductor en un rango de fechas.
     */
    public String generarReporteEntregasPorConductor(String conductorId, LocalDate inicio, LocalDate fin) {
        Optional<Conductor> conductorOpt = conductorDAO.findById(conductorId);
        String conductorNombre = conductorOpt.map(Conductor::getNombreCompleto).orElse(conductorId);

        List<Ruta> rutas = rutaDAO.findByConductor(conductorId);
        List<Ruta> rutasEnRango = new ArrayList<>();
        for (Ruta r : rutas) {
            if ((inicio == null || !r.getFecha().isBefore(inicio)) &&
                    (fin == null || !r.getFecha().isAfter(fin))) {
                rutasEnRango.add(r);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n");
        sb.append("          REPORTE OPERATIVO: ENTREGAS POR CONDUCTOR\n");
        sb.append("=========================================================================\n");
        sb.append(String.format("Conductor: [%s] %s%n", conductorId, conductorNombre));
        sb.append(String.format("Período:   %s a %s%n", inicio != null ? inicio : "Inicio", fin != null ? fin : "Actualidad"));
        sb.append(String.format("Generado:  %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        sb.append("-------------------------------------------------------------------------\n");

        int totalEntregados = 0;
        int totalDevueltos = 0;
        int totalNoEntregados = 0;
        int totalAsignados = 0;

        sb.append(String.format("%-8s | %-12s | %-12s | %-10s | %-14s | %s%n",
                "RUTA ID", "FECHA", "VEHÍCULO", "PKG ID", "ESTADO", "OBSERVACIONES"));
        sb.append("-------------------------------------------------------------------------\n");

        for (Ruta r : rutasEnRango) {
            List<RutaPaquete> paquetes = rutaPaqueteDAO.findByRutaId(r.getIdRuta());
            for (RutaPaquete rp : paquetes) {
                totalAsignados++;
                if (rp.getEstadoPaquete() == EstadoRutaPaquete.ENTREGADO) totalEntregados++;
                else if (rp.getEstadoPaquete() == EstadoRutaPaquete.DEVUELTO) totalDevueltos++;
                else if (rp.getEstadoPaquete() == EstadoRutaPaquete.NO_ENTREGADO) totalNoEntregados++;

                sb.append(String.format("%-8d | %-12s | %-12s | %-10s | %-14s | %s%n",
                        r.getIdRuta(), r.getFecha(), r.getVehiculoPlaca(),
                        rp.getPaqueteId(), rp.getEstadoPaquete(),
                        rp.getObservaciones() != null ? rp.getObservaciones() : "-"));
            }
        }

        sb.append("-------------------------------------------------------------------------\n");
        sb.append("RESUMEN:\n");
        sb.append(String.format("  Total rutas realizadas:     %d%n", rutasEnRango.size()));
        sb.append(String.format("  Total paquetes asignados:   %d%n", totalAsignados));
        sb.append(String.format("  Paquetes entregados:        %d%n", totalEntregados));
        sb.append(String.format("  Paquetes devueltos:         %d%n", totalDevueltos));
        sb.append(String.format("  Paquetes no entregados:     %d%n", totalNoEntregados));
        double efectividad = totalAsignados > 0 ? ((double) totalEntregados / totalAsignados) * 100 : 0.0;
        sb.append(String.format("  Tasa de efectividad:        %.2f%%%n", efectividad));
        sb.append("=========================================================================\n");

        return sb.toString();
    }

    /**
     * Genera el informe del historial de rutas de un vehículo.
     */
    public String generarHistorialRutasVehiculo(String placa) {
        Optional<Vehiculo> vehiculoOpt = vehiculoDAO.findById(placa);
        String detalleVehiculo = vehiculoOpt.map(v -> String.format("%s %s (Capacidad: %.2f kg)", v.getMarca(), v.getModelo(), v.getCapacidadMaxKg()))
                .orElse(placa);

        List<Ruta> rutas = rutaDAO.findByVehiculo(placa);

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n");
        sb.append("          REPORTE OPERATIVO: HISTORIAL DE RUTAS POR VEHÍCULO\n");
        sb.append("=========================================================================\n");
        sb.append(String.format("Vehículo: [%s] %s%n", placa, detalleVehiculo));
        sb.append(String.format("Generado: %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        sb.append("-------------------------------------------------------------------------\n");
        sb.append(String.format("%-8s | %-12s | %-15s | %-14s | %-10s | %s%n",
                "RUTA ID", "FECHA", "CONDUCTOR", "ESTADO RUTA", "PAQUETES", "PESO CARGA (KG)"));
        sb.append("-------------------------------------------------------------------------\n");

        BigDecimal pesoTotalHistorico = BigDecimal.ZERO;
        int totalPaquetesHistorico = 0;

        for (Ruta r : rutas) {
            int paquetesCount = rutaPaqueteDAO.countByRutaId(r.getIdRuta());
            BigDecimal pesoRuta = rutaPaqueteDAO.getPesoTotalRuta(r.getIdRuta());
            totalPaquetesHistorico += paquetesCount;
            pesoTotalHistorico = pesoTotalHistorico.add(pesoRuta);

            sb.append(String.format("%-8d | %-12s | %-15s | %-14s | %-10d | %.2f%n",
                    r.getIdRuta(), r.getFecha(), r.getConductorId(),
                    r.getEstado(), paquetesCount, pesoRuta));
        }

        sb.append("-------------------------------------------------------------------------\n");
        sb.append("RESUMEN:\n");
        sb.append(String.format("  Total rutas ejecutadas:    %d%n", rutas.size()));
        sb.append(String.format("  Total paquetes cargados:   %d%n", totalPaquetesHistorico));
        sb.append(String.format("  Peso total transportado:   %.2f kg%n", pesoTotalHistorico));
        sb.append("=========================================================================\n");

        return sb.toString();
    }

    /**
     * Genera el informe del resumen de flota y estado actual de vehículos.
     */
    public String generarResumenFlota() {
        List<Vehiculo> vehiculos = vehiculoDAO.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n");
        sb.append("          REPORTE GENERAL: ESTADO DE LA FLOTA RAPIDEXPRESS\n");
        sb.append("=========================================================================\n");
        sb.append(String.format("%-10s | %-12s | %-12s | %-6s | %-12s | %-15s%n",
                "PLACA", "MARCA", "MODELO", "AÑO", "CAPACIDAD KG", "ESTADO"));
        sb.append("-------------------------------------------------------------------------\n");

        Map<String, Integer> contadorEstados = new HashMap<>();
        BigDecimal capacidadTotal = BigDecimal.ZERO;

        for (Vehiculo v : vehiculos) {
            contadorEstados.put(v.getEstado().name(), contadorEstados.getOrDefault(v.getEstado().name(), 0) + 1);
            capacidadTotal = capacidadTotal.add(v.getCapacidadMaxKg());
            sb.append(String.format("%-10s | %-12s | %-12s | %-6d | %-12.2f | %-15s%n",
                    v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnioFabricacion(),
                    v.getCapacidadMaxKg(), v.getEstado()));
        }

        sb.append("-------------------------------------------------------------------------\n");
        sb.append("TOTALES FLOTA:\n");
        sb.append(String.format("  Total vehículos:           %d%n", vehiculos.size()));
        sb.append(String.format("  Disponibles:               %d%n", contadorEstados.getOrDefault("DISPONIBLE", 0)));
        sb.append(String.format("  En Ruta:                   %d%n", contadorEstados.getOrDefault("EN_RUTA", 0)));
        sb.append(String.format("  En Mantenimiento:          %d%n", contadorEstados.getOrDefault("EN_MANTENIMIENTO", 0)));
        sb.append(String.format("  Capacidad total conjunta:  %.2f kg%n", capacidadTotal));
        sb.append("=========================================================================\n");

        return sb.toString();
    }

    /**
     * Genera el informe del resumen de costos de mantenimiento por vehículo.
     */
    public String generarResumenCostosMantenimiento() {
        List<Mantenimiento> mantenimientos = mantenimientoDAO.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n");
        sb.append("          REPORTE DE COSTOS DE MANTENIMIENTO POR FLOTA\n");
        sb.append("=========================================================================\n");
        sb.append(String.format("%-6s | %-10s | %-12s | %-14s | %-12s | %s%n",
                "ID", "VEHÍCULO", "FECHA", "TIPO", "COSTO ($)", "PROVEEDOR"));
        sb.append("-------------------------------------------------------------------------\n");

        BigDecimal granTotal = BigDecimal.ZERO;
        Map<String, BigDecimal> costosPorVehiculo = new HashMap<>();

        for (Mantenimiento m : mantenimientos) {
            BigDecimal c = m.getCosto() != null ? m.getCosto() : BigDecimal.ZERO;
            granTotal = granTotal.add(c);
            costosPorVehiculo.put(m.getVehiculoPlaca(), costosPorVehiculo.getOrDefault(m.getVehiculoPlaca(), BigDecimal.ZERO).add(c));

            sb.append(String.format("%-6d | %-10s | %-12s | %-14s | %-12.2f | %s%n",
                    m.getIdMantenimiento(), m.getVehiculoPlaca(), m.getFecha(),
                    m.getTipo(), c, m.getProveedor() != null ? m.getProveedor() : "-"));
        }

        sb.append("-------------------------------------------------------------------------\n");
        sb.append("TOTAL ACUMULADO GASTOS MANTENIMIENTO: $" + String.format("%.2f", granTotal) + "\n");
        sb.append("-------------------------------------------------------------------------\n");
        sb.append("DESGLOSE POR VEHÍCULO:\n");
        for (Map.Entry<String, BigDecimal> entry : costosPorVehiculo.entrySet()) {
            sb.append(String.format("  Vehículo %-10s: $%.2f%n", entry.getKey(), entry.getValue()));
        }
        sb.append("=========================================================================\n");

        return sb.toString();
    }

    /**
     * Exporta el contenido de cualquier reporte a un archivo de texto en logs/.
     */
    public boolean exportarReporte(String nombreArchivo, String contenido) {
        if (!nombreArchivo.endsWith(".txt") && !nombreArchivo.endsWith(".log")) {
            nombreArchivo += ".txt";
        }
        return FileLogUtil.exportReport(nombreArchivo, contenido);
    }
}
