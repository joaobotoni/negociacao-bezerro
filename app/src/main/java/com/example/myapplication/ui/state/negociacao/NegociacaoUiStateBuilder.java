package com.example.myapplication.ui.state.negociacao;

import com.example.myapplication.ui.state.NegociacaoUiState;

public class NegociacaoUiStateBuilder implements Builder {
    private Cotacao cotacao;
    private Proposta proposta;
    private Fechamento fechamento;
    @Override
    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    @Override
    public void setProposta(Proposta proposta) {
       this.proposta = proposta;
    }

    @Override
    public void setFechamento(Fechamento fechamento) {
       this.fechamento = fechamento;
    }

    public NegociacaoUiState build(){
        return new NegociacaoUiState(cotacao, proposta, fechamento);
    }
}