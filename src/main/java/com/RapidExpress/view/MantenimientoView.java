package com.RapidExpress.view;

import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.TipoMantenimiento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public final class MantenimientoView {

    private MantenimientoView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("GESTION DE MANTENIMIENTOS");
        System.out.println("1. Registrar mantenimiento");
        System.out.println("2. Listar todos");
        System.out.println("3. Ver por vehiculo");
        System.out.println("4. Ver por fecha");
        System.out.println("5. Ver por tipo");
        System.out.println("6. Historial de vehiculo");
        System.out.println("7. Recientes (6 meses)");
        System.out.println("8. Estadisticas de costos");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }

    public static Mantenimiento readNewMantenimientoData(List<Vehiculo> vehiculos) {
        ConsoleUtil.printSubHeader("Datos del mantenimiento");
        if (vehiculos.isEmpty()) {
            ConsoleUtil.printError("No hay vehiculos registrados.");
            return null;
        }
        VehiculoView.showVehiculoList(vehiculos);
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo").toUpperCase();
        LocalDate fecha = ConsoleUtil.readDate("Fecha");
        TipoMantenimiento tipo = ConsoleUtil.readEnum(TipoMantenimiento.class, "Tipo de mantenimiento:");
        String desc = ConsoleUtil.readRequiredString("Descripcion");
        BigDecimal costo = ConsoleUtil.readBigDecimal("Costo ($)");
        String kmStr = ConsoleUtil.readString("Kilometraje (vacio si no aplica)");
        Integer km = kmStr.isEmpty() ? null : Integer.parseInt(kmStr);
        String proveedor = ConsoleUtil.readString("Proveedor (vacio si no aplica)");
        return new Mantenimiento(null, placa, fecha, tipo, desc, costo, km,
                proveedor.isEmpty() ? null : proveedor);
    }

    public static TipoMantenimiento readTipo() {
        return ConsoleUtil.readEnum(TipoMantenimiento.class, "Tipo de mantenimiento:");
    }

    public static LocalDate readFecha(String prompt) {
        return ConsoleUtil.readDate(prompt);
    }

    public static void showMantenimientoDetail(Mantenimiento m) {
        System.out.println("ID:          " + m.getIdMantenimiento());
        System.out.println("Vehiculo:    " + m.getVehiculoPlaca());
        System.out.println("Fecha:       " + m.getFecha());
        System.out.println("Tipo:        " + m.getTipo());
        System.out.println("Descripcion: " + m.getDescripcion());
        System.out.println("Costo:       $" + m.getCosto());
        System.out.println("Kilometraje: " + (m.getKilometraje() != null ? m.getKilometraje() : "N/A"));
        System.out.println("Proveedor:   " + (m.getProveedor() != null ? m.getProveedor() : "N/A"));
    }

    public static void showMantenimientoList(List<Mantenimiento> mantenimientos) {
        if (mantenimientos.isEmpty()) { ConsoleUtil.printInfo("No hay mantenimientos."); return; }
        String[][] rows = new String[mantenimientos.size()][6];
        for (int i = 0; i < mantenimientos.size(); i++) {
            Mantenimiento m = mantenimientos.get(i);
            rows[i] = new String[]{String.valueOf(m.getIdMantenimiento()), m.getVehiculoPlaca(),
                    m.getFecha().toString(), m.getTipo().getTipo(), "$" + m.getCosto(), m.getProveedor() != null ? m.getProveedor() : "-"};
        }
        ConsoleUtil.printTable(new String[]{"ID", "Vehiculo", "Fecha", "Tipo", "Costo", "Proveedor"}, rows);
    }

    public static void showMantenimientosByVehiculo(List<Mantenimiento> mantenimientos, String placa) {
        ConsoleUtil.printInfo("Mantenimientos del vehiculo " + placa + ":");
        showMantenimientoList(mantenimientos);
    }

    public static void showMantenimientosByFecha(List<Mantenimiento> mantenimientos, LocalDate fecha) {
        ConsoleUtil.printInfo("Mantenimientos del " + fecha + ":");
        showMantenimientoList(mantenimientos);
    }

    public static void showMantenimientosByTipo(List<Mantenimiento> mantenimientos, TipoMantenimiento tipo) {
        ConsoleUtil.printInfo("Mantenimientos tipo " + tipo + ":");
        showMantenimientoList(mantenimientos);
    }

    public static void showHistorialVehiculo(List<Mantenimiento> mantenimientos, Vehiculo vehiculo) {
        ConsoleUtil.printSubHeader("HISTORIAL DE " + vehiculo.getPlaca() + " - " + vehiculo.getMarca() + " " + vehiculo.getModelo());
        showMantenimientoList(mantenimientos);
        BigDecimal total = mantenimientos.stream()
                .map(Mantenimiento::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ConsoleUtil.printInfo("Total gastado: $" + total);
    }

    public static void showMantenimientosRecientes(List<Mantenimiento> mantenimientos) {
        ConsoleUtil.printInfo("Mantenimientos de los ultimos 6 meses:");
        showMantenimientoList(mantenimientos);
    }

    public static void showEstadisticasCostos(BigDecimal total, int count, BigDecimal promedio,
                                               int preventivos, int correctivos, int inspecciones) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE COSTOS");
        System.out.println("Total mantenimientos:  " + count);
        System.out.println("Costo total:           $" + total);
        System.out.println("Promedio por manten.:  $" + promedio);
        System.out.println("Preventivos:           " + preventivos);
        System.out.println("Correctivos:           " + correctivos);
        System.out.println("Inspecciones:          " + inspecciones);
    }
}