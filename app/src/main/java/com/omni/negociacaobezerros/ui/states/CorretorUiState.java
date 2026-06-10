package com.omni.negociacaobezerros.ui.states;

import java.math.BigDecimal;

public class CorretorUiState {
    private final String nome;
    private final BigDecimal comissao;
    private final String tipoComissao;
    private final boolean selecionado;
    public CorretorUiState(String nome, BigDecimal comissao, String tipoComissao, boolean selecionado) {
        this.nome = nome;
        this.comissao = comissao;
        this.tipoComissao = tipoComissao;
        this.selecionado = selecionado;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getComissao() {
        return comissao;
    }

    public String getTipoComissao() {
        return tipoComissao;
    }

    public boolean isSelecionado() {
        return selecionado;
    }
}
