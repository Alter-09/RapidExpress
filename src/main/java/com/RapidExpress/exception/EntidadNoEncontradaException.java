package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando una entidad buscada por su identificador no existe en el sistema.
 */
public class EntidadNoEncontradaException extends RapidExpressException {

    public EntidadNoEncontradaException(String entidad, String id) {
        super(String.format("No se encontró %s con identificador: %s", entidad, id));
    }

    public EntidadNoEncontradaException(String message) {
        super(message);
    }
}
