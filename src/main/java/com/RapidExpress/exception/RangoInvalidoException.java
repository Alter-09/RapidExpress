package com.RapidExpress.exception;

/**
 * Excepción lanzada cuando un valor numérico excede o no cumple las restricciones
 * de rango o verificación (CHECK) de la base de datos (ej. año [1990-2030], peso > 0, capacidad > 0, costo >= 0).
 */
public class RangoInvalidoException extends DatoInvalidoException {

    private final Object min;
    private final Object max;

    public RangoInvalidoException(String campo, Object valorIngresado, Object min, Object max, String ejemploAceptado) {
        super(campo, valorIngresado,
                String.format("El valor está fuera del rango permitido [%s - %s]",
                        min != null ? min : "-inf", max != null ? max : "+inf"),
                ejemploAceptado);
        this.min = min;
        this.max = max;
    }

    public Object getMin() {
        return min;
    }

    public Object getMax() {
        return max;
    }
}
