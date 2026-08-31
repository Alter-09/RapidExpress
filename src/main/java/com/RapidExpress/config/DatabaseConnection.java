package com.RapidExpress.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuración de conexión a la base de datos MySQL.
 * Este archivo está en el .gitignore para evitar exponer credenciales.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/RAPIDD?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Carga estática del driver JDBC de MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC de MySQL no encontrado en el classpath.");
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {
        // Constructor privado para evitar instanciación
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        System.out.println("Intentando conectar a: " + URL + " con usuario: " + USER + "...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Conexión exitosa a la base de datos MySQL [RAPIDD].");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Fallo al conectar con la base de datos: " + e.getMessage());
        }
        return false;
    }
}
