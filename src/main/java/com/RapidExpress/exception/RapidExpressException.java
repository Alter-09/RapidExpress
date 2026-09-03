package com.RapidExpress.exception;

/**
 * Excepción base para los errores de la aplicación RapidExpress.
 */
public class RapidExpressException extends Exception {

    public RapidExpressException(String message) {
        super(message);
    }

    public RapidExpressException(String message, Throwable cause) {
        super(message, cause);
    }
}
