package com.omni.negociacaobezerros.ui.states;

import java.math.BigDecimal;

public class FreteUiState {
    private final BigDecimal valorTotal;
    private final BigDecimal valorPorKg;
    public FreteUiState(BigDecimal valorTotal, BigDecimal valorPorKg) {
        this.valorTotal = valorTotal;
        this.valorPorKg = valorPorKg;

    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorPorKg() {
        return valorPorKg;
    }
}
