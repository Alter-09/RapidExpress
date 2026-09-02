package com.RapidExpress.exception;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando el peso acumulado de los paquetes excede la capacidad de carga del vehículo.
 */
public class CapacidadExcedidaException extends RapidExpressException {

    private final BigDecimal pesoTotal;
    private final BigDecimal capacidadMaxima;

    public CapacidadExcedidaException(String message, BigDecimal pesoTotal, BigDecimal capacidadMaxima) {
        super(String.format("%s (Peso total: %.2f kg, Capacidad máxima: %.2f kg)", message, pesoTotal, capacidadMaxima));
        this.pesoTotal = pesoTotal;
        this.capacidadMaxima = capacidadMaxima;
    }

    public BigDecimal getPesoTotal() {
        return pesoTotal;
    }

    public BigDecimal getCapacidadMaxima() {
        return capacidadMaxima;
    }
}
