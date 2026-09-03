package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando los datos de entrada o una regla de negocio no cumplen las validaciones.
 */
public class ValidacionException extends RapidExpressException {

    public ValidacionException(String message) {
        super(message);
    }

    public ValidacionException(String message, Throwable cause) {
        super(message, cause);
    }
}
