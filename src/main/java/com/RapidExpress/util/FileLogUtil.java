package com.RapidExpress.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para lectura y escritura de archivos planos.
 * Gestiona el archivo centralizado de auditoría (logs/auditoria.log).
 */
public class FileLogUtil {

    private static final String LOG_DIRECTORY = "logs";
    private static final String AUDIT_LOG_FILE = "logs/auditoria.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private FileLogUtil() {
    }

    /**
     * Asegura que el directorio de logs exista.
     */
    private static void ensureLogDirectory() throws IOException {
        Path dirPath = Paths.get(LOG_DIRECTORY);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    /**
     * Escribe una línea de auditoría en el archivo de texto centralizado de forma segura y append-only.
     *
     * @param operacion Operación realizada (CREATE, UPDATE, STATE_CHANGE, etc.)
     * @param entidad   Entidad afectada
     * @param entidadId Clave primaria de la entidad
     * @param usuario   Usuario u operador
     * @param detalles  Detalles en formato texto/JSON
     */
    public static synchronized void appendAuditLog(String operacion, String entidad, String entidadId,
                                                   String usuario, String detalles) {
        try {
            ensureLogDirectory();
            Path filePath = Paths.get(AUDIT_LOG_FILE);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String line = String.format("[%s] | USER: %-12s | OP: %-15s | ENTITY: %-12s | ID: %-15s | DETAILS: %s%n",
                    timestamp,
                    usuario != null ? usuario : "SYSTEM",
                    operacion,
                    entidad,
                    entidadId,
                    detalles != null ? detalles : "N/A");

            Files.writeString(filePath, line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo escribir en el archivo de log de auditoría: " + e.getMessage());
        }
    }

    /**
     * Lee todas las líneas del archivo de log de auditoría.
     *
     * @return Lista de líneas del archivo de log
     */
    public static List<String> readAuditLogs() {
        List<String> logs = new ArrayList<>();
        Path filePath = Paths.get(AUDIT_LOG_FILE);
        if (!Files.exists(filePath)) {
            return logs;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de auditoría: " + e.getMessage());
        }
        return logs;
    }

    /**
     * Exporta texto plano a un archivo arbitrario en la carpeta logs/
     */
    public static boolean exportReport(String fileName, String content) {
        try {
            ensureLogDirectory();
            Path target = Paths.get(LOG_DIRECTORY, fileName);
            Files.writeString(target, content, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error al exportar reporte a archivo: " + e.getMessage());
            return false;
        }
    }
}
