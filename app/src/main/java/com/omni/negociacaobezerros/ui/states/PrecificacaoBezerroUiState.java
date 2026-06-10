package com.omni.negociacaobezerros.ui.states;

import java.math.BigDecimal;

public class PrecificacaoBezerroUiState {
    private final BigDecimal valorPorQuilo;
    private final BigDecimal valorPorCabeca;
    public PrecificacaoBezerroUiState(BigDecimal valorPorQuilo, BigDecimal valorPorCabeca) {
        this.valorPorQuilo = valorPorQuilo;
        this.valorPorCabeca = valorPorCabeca;
    }

    public BigDecimal getValorPorQuilo() {
        return valorPorQuilo;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }
}
