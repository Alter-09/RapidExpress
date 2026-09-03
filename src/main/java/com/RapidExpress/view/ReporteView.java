package com.RapidExpress.view;

import java.time.LocalDate;

public final class ReporteView {

    private ReporteView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("MODULO DE REPORTES Y AUDITORIA");
        System.out.println("1. Resumen de Flota y Capacidad (Detallado)");
        System.out.println("2. Reporte de Conductores (General)");
        System.out.println("3. Paquetes en Bodega");
        System.out.println("4. Rutas en Curso");
        System.out.println("5. Reporte de Costos de Mantenimiento por Flota");
        System.out.println("6. Últimas Auditorías (Base de Datos)");
        System.out.println("7. Reporte Operativo: Entregas por Conductor (Rango de Fechas)");
        System.out.println("8. Reporte Operativo: Historial de Rutas por Vehículo");
        System.out.println("9. Ver Registro Centralizado de Auditoría (Archivo logs/auditoria.log)");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }

    public static String readConductorId(String prompt) {
        return ConsoleUtil.readRequiredString(prompt);
    }

    public static String readVehiculoPlaca(String prompt) {
        return ConsoleUtil.readRequiredString(prompt).toUpperCase();
    }

    public static LocalDate readFechaInicio() {
        return ConsoleUtil.readDate("Fecha inicial del período", "01/01/2024");
    }

    public static LocalDate readFechaFin() {
        return ConsoleUtil.readDate("Fecha final del período", "31/12/2024");
    }

    public static boolean confirmExportar() {
        return ConsoleUtil.readConfirmation("¿Desea exportar este reporte a un archivo de texto en logs/?");
    }

    public static String readNombreArchivo(String defaultName) {
        return ConsoleUtil.readOptionalString("Nombre del archivo", defaultName, defaultName);
    }
}