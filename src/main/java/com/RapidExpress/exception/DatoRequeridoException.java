package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando un campo obligatorio (NOT NULL en base de datos)
 * no es proporcionado o se ingresa como cadena vacía.
 */
public class DatoRequeridoException extends ValidacionException {

    private final String campo;
    private final String ejemploAceptado;

    public DatoRequeridoException(String campo, String ejemploAceptado) {
        super(String.format("El campo '%s' es obligatorio y no puede estar vacío. Ejemplo aceptado: %s",
                campo, ejemploAceptado));
        this.campo = campo;
        this.ejemploAceptado = ejemploAceptado;
    }

    public String getCampo() {
        return campo;
    }

    public String getEjemploAceptado() {
        return ejemploAceptado;
    }
}
