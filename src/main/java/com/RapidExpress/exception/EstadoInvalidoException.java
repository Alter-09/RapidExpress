package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando una operación no es válida para el estado actual de una entidad.
 */
public class EstadoInvalidoException extends RapidExpressException {

    public EstadoInvalidoException(String message) {
        super(message);
    }
}
