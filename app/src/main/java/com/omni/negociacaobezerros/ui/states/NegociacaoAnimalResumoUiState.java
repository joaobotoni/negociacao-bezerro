package com.omni.negociacaobezerros.ui.states;

import java.math.BigDecimal;

public class NegociacaoAnimalResumoUiState {
    private final int quantidadeTotal;
    private final int progresso;
    private final int percentual;
    private final int faltam;
    private final double pesoMedioKg;
    private final BigDecimal valorTotal;
    private final boolean todosAnimaisPesados;

    public NegociacaoAnimalResumoUiState(int quantidadeTotal, int progresso, int percentual, int faltam,
                                          double pesoMedioKg, BigDecimal valorTotal, boolean todosAnimaisPesados) {
        this.quantidadeTotal = quantidadeTotal;
        this.progresso = progresso;
        this.percentual = percentual;
        this.faltam = faltam;
        this.pesoMedioKg = pesoMedioKg;
        this.valorTotal = valorTotal;
        this.todosAnimaisPesados = todosAnimaisPesados;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public int getProgresso() {
        return progresso;
    }

    public int getPercentual() {
        return percentual;
    }

    public int getFaltam() {
        return faltam;
    }

    public double getPesoMedioKg() {
        return pesoMedioKg;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public boolean isTodosAnimaisPesados() {
        return todosAnimaisPesados;
    }
}
