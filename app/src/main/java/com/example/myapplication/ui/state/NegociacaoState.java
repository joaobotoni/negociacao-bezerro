package com.example.myapplication.ui.state;

import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.state.negociacao.FechamentoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;

public class NegociacaoState {
    private final CotacaoState cotacao;
    private final PropostaState propostaState;
    private final FechamentoState fechamento;
    public NegociacaoState(CotacaoState cotacao, PropostaState propostaState, FechamentoState fechamento) {
        this.cotacao = cotacao;
        this.propostaState = propostaState;
        this.fechamento = fechamento;
    }

    public CotacaoState getCotacao() {
        return cotacao;
    }

    public PropostaState getProposta() {
        return propostaState;
    }

    public FechamentoState getFechamento() {
        return fechamento;
    }
}