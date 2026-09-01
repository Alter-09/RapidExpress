package com.RapidExpress.view;

public final class MainMenuView {

    private MainMenuView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("MENU PRINCIPAL");
        System.out.println("1. Gestion de vehiculos");
        System.out.println("2. Gestion de conductores");
        System.out.println("3. Gestion de paquetes");
        System.out.println("4. Gestion de rutas");
        System.out.println("5. Gestion de mantenimientos");
        System.out.println("6. Reportes");
        System.out.println("0. Salir");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }
}