package com.omni.negociacaobezerros.data.models;

import java.math.BigDecimal;

public class ParametrosBezerro {
    public final BigDecimal pesoBaseKg;
    public final BigDecimal precoPorArroba;
    public final BigDecimal percentualAgio;

    public ParametrosBezerro(BigDecimal pesoBaseKg, BigDecimal precoPorArroba, BigDecimal percentualAgio) {
        this.pesoBaseKg = pesoBaseKg;
        this.precoPorArroba = precoPorArroba;
        this.percentualAgio = percentualAgio;
    }
}
