package com.RapidExpress.exception;

/**
 * Excepción base para datos ingresados que no cumplen con los formatos,
 * rangos o restricciones requeridas por la base de datos de RapidExpress.
 */
public class DatoInvalidoException extends ValidacionException {

    private final String campo;
    private final Object valorIngresado;
    private final String ejemploAceptado;

    public DatoInvalidoException(String campo, Object valorIngresado, String motivo, String ejemploAceptado) {
        super(String.format("Dato inválido para el campo '%s' (Valor recibido: '%s'). %s. Ejemplo aceptado: %s",
                campo, valorIngresado != null ? valorIngresado : "nulo", motivo, ejemploAceptado));
        this.campo = campo;
        this.valorIngresado = valorIngresado;
        this.ejemploAceptado = ejemploAceptado;
    }

    public String getCampo() {
        return campo;
    }

    public Object getValorIngresado() {
        return valorIngresado;
    }

    public String getEjemploAceptado() {
        return ejemploAceptado;
    }
}
