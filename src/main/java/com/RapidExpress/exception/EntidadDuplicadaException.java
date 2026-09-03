package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando se intenta registrar una entidad con un identificador o restricción única que ya existe.
 */
public class EntidadDuplicadaException extends RapidExpressException {

    public EntidadDuplicadaException(String entidad, String clave) {
        super(String.format("Ya existe un registro de %s con el valor único: %s", entidad, clave));
    }

    public EntidadDuplicadaException(String message) {
        super(message);
    }
}
