package com.omni.negociacaobezerros.ui.states;

import java.math.BigDecimal;

public class CotacaoUiState {
    private final BigDecimal valorPorQuilo;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;

    public CotacaoUiState(BigDecimal valorPorQuilo, BigDecimal valorPorCabeca, BigDecimal valorTotal) {
        this.valorPorQuilo = valorPorQuilo;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
    }


    public BigDecimal getValorPorQuilo() {
        return valorPorQuilo;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }
}
