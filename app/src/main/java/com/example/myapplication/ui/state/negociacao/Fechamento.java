package com.example.myapplication.ui.state.negociacao;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Fechamento {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;
    private final BigDecimal comissaoPorKg;
    private final boolean isComissaoAplicada;
    private final double variacaoPercentual;

    public Fechamento(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal comissaoPorKg, double variacaoPercentual) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
        this.comissaoPorKg = comissaoPorKg;
        this.isComissaoAplicada = comissaoPorKg != null;
        this.variacaoPercentual = variacaoPercentual;
    }

    public BigDecimal getValorPorKg() { return valorPorKg; }
    public BigDecimal getValorPorCabeca() { return valorPorCabeca; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public BigDecimal getComissaoPorKg() { return comissaoPorKg; }
    public double getVariacaoPercentual() {return variacaoPercentual;}
    public boolean isComissaoAplicada() { return isComissaoAplicada; }
}
