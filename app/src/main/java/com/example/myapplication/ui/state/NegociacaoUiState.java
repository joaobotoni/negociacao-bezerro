package com.example.myapplication.ui.state;

import com.example.myapplication.ui.state.negociacao.Cotacao;
import com.example.myapplication.ui.state.negociacao.Fechamento;
import com.example.myapplication.ui.state.negociacao.Proposta;

public class NegociacaoUiState {
    private final Cotacao cotacao;
    private final Proposta proposta;
    private final Fechamento fechamento;
    public NegociacaoUiState(Cotacao cotacao, Proposta proposta, Fechamento fechamento) {
        this.cotacao = cotacao;
        this.proposta = proposta;
        this.fechamento = fechamento;
    }

    public Cotacao getCotacao() {
        return cotacao;
    }

    public Proposta getProposta() {
        return proposta;
    }

    public Fechamento getFechamento() {
        return fechamento;
    }
}