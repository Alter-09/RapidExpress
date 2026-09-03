package com.RapidExpress.util;

import com.RapidExpress.exception.DatoRequeridoException;
import com.RapidExpress.exception.FormatoInvalidoException;
import com.RapidExpress.exception.RangoInvalidoException;
import com.RapidExpress.model.enums.TipoLicencia;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validador centralizado de datos aceptados por la base de datos de RapidExpress.
 * Guiado estrictamente por las definiciones y muestras de database/README.md.
 */
public final class DataValidator {

    // Patrones regex basados en README.md
    private static final Pattern PATTERN_PLACA = Pattern.compile("^[A-Z]{3}-\\d{3}$|^[A-Z0-9-]{3,10}$");
    private static final Pattern PATTERN_IDENTIFICACION_CONDUCTOR = Pattern.compile("^\\d{4,20}$");
    private static final Pattern PATTERN_NOMBRE_COMPLETO = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+(?:[\\s.'-]+[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+)+$");
    private static final Pattern PATTERN_CONTACTO = Pattern.compile("^\\d{7,15}$");
    private static final Pattern PATTERN_ID_SEGUIMIENTO = Pattern.compile("^PKG-\\d{8}-\\w{3,4}$|^[a-zA-Z0-9-]{8,36}$");
    private static final Pattern PATTERN_DIMENSIONES = Pattern.compile("^\\d+(\\.\\d+)?x\\d+(\\.\\d+)?x\\d+(\\.\\d+)?$");

    private DataValidator() {}

    /**
     * Valida la placa de un vehículo según VARCHAR(10) y formato representativo (ej: ABC-123).
     */
    public static String validarPlaca(String placa) throws DatoRequeridoException, FormatoInvalidoException {
        if (placa == null || placa.trim().isEmpty()) {
            throw new DatoRequeridoException("Placa", "ABC-123");
        }
        String p = placa.trim().toUpperCase();
        if (p.length() > 10 || !PATTERN_PLACA.matcher(p).matches()) {
            throw new FormatoInvalidoException("Placa", placa, "Hasta 10 caracteres alfanuméricos con guión (ej: ABC-123)", "ABC-123");
        }
        return p;
    }

    /**
     * Valida el año de fabricación según la restricción chk_vehiculo_anio (1990 - 2030).
     */
    public static int validarAnioVehiculo(int anio) throws RangoInvalidoException {
        if (anio < 1990 || anio > 2030) {
            throw new RangoInvalidoException("Año de fabricación", anio, 1990, 2030, "2020");
        }
        return anio;
    }

    /**
     * Valida la capacidad máxima de carga en kg según chk_vehiculo_capacidad (> 0 kg).
     */
    public static BigDecimal validarCapacidadVehiculo(BigDecimal cap) throws DatoRequeridoException, RangoInvalidoException {
        if (cap == null) {
            throw new DatoRequeridoException("Capacidad máxima", "5000.00");
        }
        if (cap.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RangoInvalidoException("Capacidad máxima (kg)", cap, "mayor a 0.00", "22000.00", "5000.00");
        }
        return cap;
    }

    /**
     * Valida el número de identificación del conductor según VARCHAR(20) y formato numérico representativo (ej: 1000000001).
     */
    public static String validarIdentificacionConductor(String id) throws DatoRequeridoException, FormatoInvalidoException {
        if (id == null || id.trim().isEmpty()) {
            throw new DatoRequeridoException("Número de identificación", "1000000001");
        }
        String limpio = id.trim();
        if (limpio.length() > 20 || !PATTERN_IDENTIFICACION_CONDUCTOR.matcher(limpio).matches() || limpio.matches("^0+$")) {
            throw new FormatoInvalidoException("Número de identificación", id,
                    "Entre 4 y 20 dígitos numéricos (ej: 1000000001)", "1000000001");
        }
        return limpio;
    }

    /**
     * Valida el nombre completo del conductor según VARCHAR(100) y formato representativo (ej: Carlos Andres Rodriguez).
     */
    public static String validarNombreCompleto(String nombre) throws DatoRequeridoException, FormatoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoRequeridoException("Nombre completo", "Carlos Andres Rodriguez");
        }
        String limpio = nombre.trim().replaceAll("\\s+", " ");
        if (limpio.length() < 3 || limpio.length() > 100 || !PATTERN_NOMBRE_COMPLETO.matcher(limpio).matches()) {
            throw new FormatoInvalidoException("Nombre completo", nombre,
                    "Nombre y apellido válidos usando únicamente letras y acentos (hasta 100 caracteres)", "Carlos Andres Rodriguez");
        }
        return limpio;
    }

    /**
     * Valida el tipo de licencia del conductor según las categorías de transporte de database/README.md (C, D, E).
     */
    public static TipoLicencia validarTipoLicenciaConductor(TipoLicencia tipo) throws DatoRequeridoException, FormatoInvalidoException {
        if (tipo == null) {
            throw new DatoRequeridoException("Tipo de licencia", "C");
        }
        if (tipo != TipoLicencia.C && tipo != TipoLicencia.D && tipo != TipoLicencia.E) {
            throw new FormatoInvalidoException("Tipo de licencia", tipo.getCodigo(),
                    "Categoría válida para transporte de carga según base de datos (C, D, E)", "C");
        }
        return tipo;
    }

    /**
     * Valida el número de contacto telefónico según VARCHAR(20) y formato numérico (ej: 3001112233).
     */
    public static String validarContacto(String contacto, String campo) throws DatoRequeridoException, FormatoInvalidoException {
        String nombreCampo = campo != null ? campo : "Número de contacto";
        if (contacto == null || contacto.trim().isEmpty()) {
            throw new DatoRequeridoException(nombreCampo, "3001112233");
        }
        String limpio = contacto.trim().replaceAll("\\s+", "");
        if (!PATTERN_CONTACTO.matcher(limpio).matches() || limpio.matches("^0+$")) {
            throw new FormatoInvalidoException(nombreCampo, contacto, "Entre 7 y 15 dígitos numéricos válidos", "3001112233");
        }
        return limpio;
    }

    /**
     * Valida el ID de seguimiento de un paquete según VARCHAR(36) y formato PKG-YYYYMMDD-XXX.
     */
    public static String validarIdSeguimiento(String idSeguimiento) throws DatoRequeridoException, FormatoInvalidoException {
        if (idSeguimiento == null || idSeguimiento.trim().isEmpty()) {
            throw new DatoRequeridoException("ID de seguimiento", "PKG-20240115-001");
        }
        String limpio = idSeguimiento.trim().toUpperCase();
        if (limpio.length() > 36 || !PATTERN_ID_SEGUIMIENTO.matcher(limpio).matches()) {
            throw new FormatoInvalidoException("ID de seguimiento", idSeguimiento,
                    "Formato estandarizado PKG-YYYYMMDD-XXX o identificador de hasta 36 caracteres", "PKG-20240115-001");
        }
        return limpio;
    }

    /**
     * Valida el peso del paquete según chk_paquete_peso (> 0 kg).
     */
    public static BigDecimal validarPesoPaquete(BigDecimal peso) throws DatoRequeridoException, RangoInvalidoException {
        if (peso == null) {
            throw new DatoRequeridoException("Peso (kg)", "2.50");
        }
        if (peso.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RangoInvalidoException("Peso (kg)", peso, "mayor a 0.00", "200.00", "2.50");
        }
        return peso;
    }

    /**
     * Valida las dimensiones del paquete según formato Ancho x Alto x Profundidad (ej: 30x20x5).
     */
    public static String validarDimensiones(String dimensiones) throws DatoRequeridoException, FormatoInvalidoException {
        if (dimensiones == null || dimensiones.trim().isEmpty()) {
            throw new DatoRequeridoException("Dimensiones", "30x20x5");
        }
        String limpio = dimensiones.trim().toLowerCase().replaceAll("\\s+", "");
        if (limpio.length() > 50 || !PATTERN_DIMENSIONES.matcher(limpio).matches()) {
            throw new FormatoInvalidoException("Dimensiones", dimensiones,
                    "Formato Ancho x Alto x Profundidad en cm (ej: 30x20x5)", "30x20x5");
        }
        return limpio;
    }

    /**
     * Valida el costo de mantenimiento (> 0 COP).
     */
    public static BigDecimal validarCostoMantenimiento(BigDecimal costo) throws DatoRequeridoException, RangoInvalidoException {
        if (costo == null) {
            throw new DatoRequeridoException("Costo ($)", "450000.00");
        }
        if (costo.compareTo(BigDecimal.ZERO) < 0) {
            throw new RangoInvalidoException("Costo ($)", costo, "mayor o igual a 0.00", "3500000.00", "450000.00");
        }
        return costo;
    }

    /**
     * Valida el kilometraje si se ingresa (debe ser >= 0).
     */
    public static Integer validarKilometraje(Integer km) throws RangoInvalidoException {
        if (km != null && km < 0) {
            throw new RangoInvalidoException("Kilometraje", km, 0, "+inf", "150000");
        }
        return km;
    }

    /**
     * Valida texto obligatorio no vacío respetando un tamaño máximo de VARCHAR.
     */
    public static String validarTextoRequerido(String valor, String campo, int maxLen, String ejemploAceptado)
            throws DatoRequeridoException, FormatoInvalidoException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatoRequeridoException(campo, ejemploAceptado);
        }
        String limpio = valor.trim();
        if (limpio.length() > maxLen) {
            throw new FormatoInvalidoException(campo, valor, "Máximo " + maxLen + " caracteres", ejemploAceptado);
        }
        return limpio;
    }
}
