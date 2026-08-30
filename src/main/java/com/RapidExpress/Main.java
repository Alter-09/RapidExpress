package com.RapidExpress;

import com.RapidExpress.config.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("  RapidExpress - Sistema de Gestión de Flotas y Rutas    ");
        System.out.println("=========================================================");

        // Verificación de conexión a la base de datos
        DatabaseConnection.testConnection();
    }
}

