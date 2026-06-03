package com.omni.negociacaobezerros.data.models;

import java.math.BigDecimal;

public class PrecificacaoFrete {
    private final BigDecimal valorTotal;
    private final BigDecimal valorPorKg;

    public PrecificacaoFrete(BigDecimal valorTotal, BigDecimal valorPorKg) {
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
