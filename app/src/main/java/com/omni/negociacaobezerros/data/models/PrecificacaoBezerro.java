package com.omni.negociacaobezerros.data.models;

import java.math.BigDecimal;

public class PrecificacaoBezerro {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;
    private final int quantidade;

    public PrecificacaoBezerro(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, int quantidade) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
        this.quantidade = quantidade;
    }

    public BigDecimal getValorPorKg() {
        return valorPorKg;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
