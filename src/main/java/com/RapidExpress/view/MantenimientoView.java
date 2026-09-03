package com.RapidExpress.view;

import com.RapidExpress.model.entity.Mantenimiento;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.TipoMantenimiento;
import com.RapidExpress.util.DataValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista de consola para la gestión de mantenimientos mecánicos y revisiones.
 * Muestra ejemplos representativos y valida datos según database/README.md.
 */
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
        ConsoleUtil.printSubHeader("DATOS DEL MANTENIMIENTO");
        if (vehiculos.isEmpty()) {
            ConsoleUtil.printError("No hay vehículos registrados para programar mantenimiento.");
            return null;
        }

        VehiculoView.showVehiculoList(vehiculos);
        ConsoleUtil.printInfo("Formato de datos aceptados: Placa (ej: ABC-123), Fecha (dd/MM/yyyy), Costo > 0 COP (ej: 450000.00)");

        String placa = ConsoleUtil.readValidatedString("Placa del vehículo", "ABC-123", DataValidator::validarPlaca);
        LocalDate fecha = ConsoleUtil.readDate("Fecha de realización", "15/06/2023");
        TipoMantenimiento tipo = ConsoleUtil.readEnum(TipoMantenimiento.class, "Seleccione el tipo de mantenimiento:");
        String desc = ConsoleUtil.readRequiredString("Descripción del trabajo realizado", "Cambio de aceite y filtros");
        BigDecimal costo = ConsoleUtil.readPositiveBigDecimal("Costo del servicio en COP", "450000.00");

        String kmStr = ConsoleUtil.readString("Kilometraje del vehículo [opcional] (ej: 150000, o ENTER si no aplica)");
        Integer km = null;
        if (!kmStr.isEmpty()) {
            try {
                km = DataValidator.validarKilometraje(Integer.parseInt(kmStr));
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage() + ". Se registrará sin kilometraje.");
                km = null;
            }
        }

        String proveedor = ConsoleUtil.readString("Proveedor o taller [opcional] (ej: Chevrolet Servicio Autorizado)");
        return new Mantenimiento(null, placa, fecha, tipo, desc, costo, km,
                proveedor.isEmpty() ? null : proveedor);
    }

    public static TipoMantenimiento readTipo() {
        return ConsoleUtil.readEnum(TipoMantenimiento.class, "Tipo de mantenimiento:");
    }

    public static LocalDate readFecha(String prompt) {
        return ConsoleUtil.readDate(prompt, "15/06/2023");
    }

    public static void showMantenimientoDetail(Mantenimiento m) {
        System.out.println("ID:          " + m.getIdMantenimiento());
        System.out.println("Vehículo:    " + m.getVehiculoPlaca());
        System.out.println("Fecha:       " + m.getFecha());
        System.out.println("Tipo:        " + m.getTipo());
        System.out.println("Descripción: " + m.getDescripcion());
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
        ConsoleUtil.printTable(new String[]{"ID", "Vehículo", "Fecha", "Tipo", "Costo", "Proveedor"}, rows);
    }

    public static void showMantenimientosByVehiculo(List<Mantenimiento> mantenimientos, String placa) {
        ConsoleUtil.printInfo("Mantenimientos del vehículo " + placa + ":");
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
        ConsoleUtil.printInfo("Mantenimientos de los últimos 6 meses:");
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