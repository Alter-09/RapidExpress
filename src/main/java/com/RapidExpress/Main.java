package com.RapidExpress;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.controller.*;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.MainMenuView;

/**
 * Entry point - menu loop dispatching to controllers.
 */
public class Main {

    public static void main(String[] args) {
        ConsoleUtil.printHeader("RapidExpress - Sistema de Gestion de Flotas y Rutas");

        // Verificación inicial de conexión a la base de datos
        boolean dbOk = DatabaseConnection.testConnection();
        if (!dbOk) {
            ConsoleUtil.printError("La aplicacion se inicio SIN conexion activa a la base de datos.");
            ConsoleUtil.printInfo("Las operaciones CRUD fallaran hasta que se inicie el servicio MySQL.");
        }

        VehiculoController vehiculo = new VehiculoController();
        ConductorController conductor = new ConductorController();
        PaqueteController paquete = new PaqueteController();
        RutaController ruta = new RutaController();
        MantenimientoController mantenimiento = new MantenimientoController();
        ReporteController reporte = new ReporteController();

        boolean running = true;
        while (running) {
            MainMenuView.showMenu();
            int opcion = MainMenuView.readMenuOption();
            ConsoleUtil.clearScreen();

            switch (opcion) {
                case 1 -> vehiculo.run();
                case 2 -> conductor.run();
                case 3 -> paquete.run();
                case 4 -> ruta.run();
                case 5 -> mantenimiento.run();
                case 6 -> reporte.run();
                case 0 -> running = false;
                default -> ConsoleUtil.printError("Opcion no valida.");
            }
            if (running && opcion != 0) ConsoleUtil.pressEnterToContinue();
        }
        ConsoleUtil.printInfo("Sistema finalizado. Hasta luego!");
    }
}

