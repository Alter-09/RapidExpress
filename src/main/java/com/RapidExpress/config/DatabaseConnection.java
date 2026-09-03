package com.RapidExpress.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuración de conexión a la base de datos MySQL.
 * Permite configuración mediante variables de entorno, propiedades del sistema o valores por defecto.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String DEFAULT_HOST = "";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "RAPIDD";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String getEnvOrProp(String envKey, String propKey, String defaultValue) {
        String val = System.getProperty(propKey);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        val = System.getenv(envKey);
        if (val != null && !val.trim().isEmpty()) {
            return val.trim();
        }
        return defaultValue;
    }

    public static String getUrl() {
        String customUrl = getEnvOrProp("DB_URL", "db.url", null);
        if (customUrl != null) {
            return customUrl;
        }
        String host = getEnvOrProp("DB_HOST", "db.host", DEFAULT_HOST);
        String port = getEnvOrProp("DB_PORT", "db.port", DEFAULT_PORT);
        String dbName = getEnvOrProp("DB_NAME", "db.name", DEFAULT_DB);
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8", host, port, dbName);
    }

    public static String getUser() {
        return getEnvOrProp("DB_USER", "db.user", DEFAULT_USER);
    }

    public static String getPassword() {
        return getEnvOrProp("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);
    }

    // Carga estática del driver JDBC de MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC de MySQL (com.mysql.cj.jdbc.Driver) no encontrado en el classpath.", e);
            throw new ExceptionInInitializerError("Error: Driver JDBC de MySQL no encontrado en el classpath: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
        // Constructor privado para evitar instanciación
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
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
        String url = getUrl();
        String user = getUser();
        System.out.println("\n[INFO] Verificando conexion con la base de datos MySQL...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[OK] Conexion exitosa a la base de datos MySQL [" + getEnvOrProp("DB_NAME", "db.name", DEFAULT_DB) + "] (" + getEnvOrProp("DB_HOST", "db.host", DEFAULT_HOST) + ":" + getEnvOrProp("DB_PORT", "db.port", DEFAULT_PORT) + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudo conectar a la base de datos MySQL: " + e.getMessage());
            diagnoseError(e);
        }
        return false;
    }

    private static void diagnoseError(SQLException e) {
        String message = e.getMessage() != null ? e.getMessage() : "";
        int errorCode = e.getErrorCode();
        System.err.println("\n========== DIAGNÓSTICO DE CONEXIÓN RAPIDEXPRESS ==========");
        System.err.println("URL configurada: " + getUrl());
        System.err.println("Usuario configurado: " + getUser());
        System.err.println("Código de error SQL: " + errorCode);
        System.err.println("Mensaje SQL: " + message);

        if (message.contains("Communications link failure") || message.contains("Connection refused") || errorCode == 0) {
            System.err.println("\n[CAUSA PROBABLE]: El servicio MySQL no está iniciado o el puerto/host es inaccesible.");
            System.err.println("[SOLUCIÓN]:");
            System.err.println(" 1. Si utiliza XAMPP/WAMP/Laragon, inicie el servicio MySQL desde el panel de control.");
            System.err.println(" 2. Si utiliza MySQL como servicio de Windows, inícielo en services.msc.");
            System.err.println(" 3. Si MySQL corre en otro puerto (ej. 3307), configure la variable DB_PORT o pase -Ddb.port=3307.");
        } else if (errorCode == 1045 || message.contains("Access denied")) {
            System.err.println("\n[CAUSA PROBABLE]: Credenciales de acceso incorrectas.");
            System.err.println("[SOLUCIÓN]: Configure las variables DB_USER y DB_PASSWORD o edite DatabaseConnection.java.");
        } else if (errorCode == 1049 || message.contains("Unknown database")) {
            System.err.println("\n[CAUSA PROBABLE]: La base de datos no existe.");
            System.err.println("[SOLUCIÓN]: Ejecute el script 'database/1_schema_ddl.sql' en su cliente MySQL.");
        }
        System.err.println("==========================================================\n");
    }
}
