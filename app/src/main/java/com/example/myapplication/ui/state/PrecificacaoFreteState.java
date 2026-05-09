package com.example.myapplication.ui.state;

import java.math.BigDecimal;

public class PrecificacaoFreteState {
    private final BigDecimal valorTotal;
    private final BigDecimal valorParcial;
    private final FreteState freteState;

    public PrecificacaoFreteState(BigDecimal valorTotal, BigDecimal valorParcial) {
        this(valorTotal, valorParcial, FreteState.SIMULADO);
    }

    public PrecificacaoFreteState(BigDecimal valorTotal, BigDecimal valorParcial, FreteState freteState) {
        this.valorTotal = valorTotal;
        this.valorParcial = valorParcial;
        this.freteState = freteState;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorParcial() {
        return valorParcial;
    }

    public FreteState getFreteState() {
        return freteState;
    }
}