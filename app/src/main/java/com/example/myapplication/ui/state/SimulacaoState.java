package com.example.myapplication.ui.state;

import java.math.BigDecimal;

public class SimulacaoState {
    private final BigDecimal valorTotal;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorPorKg;

    private final int quantidade;
    public SimulacaoState(BigDecimal valorTotal, BigDecimal valorPorCabeca, BigDecimal valorPorKg, int quantidade) {
        this.valorTotal = valorTotal;
        this.valorPorCabeca = valorPorCabeca;
        this.valorPorKg = valorPorKg;
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }

    public BigDecimal getValorPorKg() {
        return valorPorKg;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
