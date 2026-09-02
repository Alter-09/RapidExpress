package com.RapidExpress.view;

public final class ReporteView {

    private ReporteView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("REPORTES");
        System.out.println("1. Vehiculos (todos)");
        System.out.println("2. Conductores (todos)");
        System.out.println("3. Paquetes en bodega");
        System.out.println("4. Rutas en curso");
        System.out.println("5. Mantenimientos por vehiculo");
        System.out.println("6. Ultimas auditorias");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }
}