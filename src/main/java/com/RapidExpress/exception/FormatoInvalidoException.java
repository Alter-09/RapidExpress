package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando el formato sintáctico o patrón de un dato
 * no coincide con el estándar de la base de datos (ej. placa, dimensiones, id de seguimiento, teléfono).
 */
public class FormatoInvalidoException extends DatoInvalidoException {

    public FormatoInvalidoException(String campo, Object valorIngresado, String formatoEsperado, String ejemploAceptado) {
        super(campo, valorIngresado, "Formato incorrecto. Debe cumplir: " + formatoEsperado, ejemploAceptado);
    }
}
