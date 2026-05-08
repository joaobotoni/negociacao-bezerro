package com.example.myapplication.ui.state;

import java.math.BigDecimal;

public class PrecificacaoFreteState {
    private final BigDecimal valorTotal;
    private final BigDecimal valorParcial;

    public PrecificacaoFreteState(BigDecimal valorTotal, BigDecimal valorParcial) {
        this.valorTotal = valorTotal;
        this.valorParcial = valorParcial;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorParcial() {
        return valorParcial;
    }
}
