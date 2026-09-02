package com.RapidExpress.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuración de conexión a la base de datos MySQL.
 * Este archivo está en el .gitignore para evitar exponer credenciales.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/RAPIDD?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Carga estática del driver JDBC de MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC de MySQL no encontrado en el classpath.", e);
            throw new ExceptionInInitializerError("Error: Driver JDBC de MySQL no encontrado en el classpath: " + e.getMessage());
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
                LOGGER.log(Level.WARNING, "Error al cerrar la conexión: " + e.getMessage(), e);
            }
        }
    }

    public static boolean testConnection() {
        LOGGER.info(() -> "Intentando conectar a: " + URL + " con usuario: " + USER + "...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("Conexión exitosa a la base de datos MySQL [RAPIDD].");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fallo al conectar con la base de datos: " + e.getMessage(), e);
        }
        return false;
    }
}
