package com.example.myapplication.ui.state.negociacao;

public interface Builder {
    void setCotacao(CotacaoState cotacao);
    void setProposta(PropostaState propostaState);
    void setFechamento(FechamentoState fechamento);
}
