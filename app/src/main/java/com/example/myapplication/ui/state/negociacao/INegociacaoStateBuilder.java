package com.example.myapplication.ui.state.negociacao;

public interface INegociacaoStateBuilder {
    void setCotacao(CotacaoState cotacao);
    void setProposta(PropostaState propostaState);
    void setFechamento(FechamentoState fechamento);
}
