package com.example.myapplication.ui.state.negociacao;

import com.example.myapplication.ui.state.NegociacaoState;

public class NegociacaoStateBuilder implements Builder {
    private CotacaoState cotacao;
    private PropostaState propostaState;
    private FechamentoState fechamento;
    @Override
    public void setCotacao(CotacaoState cotacao) {
        this.cotacao = cotacao;
    }

    @Override
    public void setProposta(PropostaState propostaState) {
       this.propostaState = propostaState;
    }

    @Override
    public void setFechamento(FechamentoState fechamento) {
       this.fechamento = fechamento;
    }

    public NegociacaoState build(){
        return new NegociacaoState(cotacao, propostaState, fechamento);
    }
}