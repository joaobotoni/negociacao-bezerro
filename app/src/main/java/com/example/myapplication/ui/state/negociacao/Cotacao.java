package com.example.myapplication.ui.state.negociacao;

import java.math.BigDecimal;

public class Cotacao {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;

    public Cotacao(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
    }
    public BigDecimal getValorPorKg() { return valorPorKg; }
    public BigDecimal getValorPorCabeca() { return valorPorCabeca; }

    public BigDecimal getValorTotal() {return valorTotal;}
}
