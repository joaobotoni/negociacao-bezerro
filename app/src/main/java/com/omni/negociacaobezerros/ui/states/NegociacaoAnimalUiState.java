package com.omni.negociacaobezerros.ui.states;

import java.math.BigDecimal;

public class NegociacaoAnimalUiState {
    private final int id;
    private final double peso;
    private final BigDecimal valorPorQuilo;
    private final BigDecimal valorPorCabeca;

    public NegociacaoAnimalUiState(int id, double peso, BigDecimal valorPorQuilo, BigDecimal valorPorCabeca) {
        this.id = id;
        this.peso = peso;
        this.valorPorQuilo = valorPorQuilo;
        this.valorPorCabeca = valorPorCabeca;
    }

    public int getId() {
        return id;
    }

    public double getPeso() {
        return peso;
    }

    public BigDecimal getValorPorQuilo() {
        return valorPorQuilo;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }
}
