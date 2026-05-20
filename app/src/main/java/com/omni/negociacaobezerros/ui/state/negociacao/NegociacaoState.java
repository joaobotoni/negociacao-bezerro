package com.omni.negociacaobezerros.ui.state.negociacao;


public class NegociacaoState {
    private final CotacaoState cotacao;
    private final PropostaState proposta;
    private final FechamentoState fechamento;


    public NegociacaoState(CotacaoState cotacao, PropostaState proposta, FechamentoState fechamento) {
        this.cotacao = cotacao;
        this.proposta = proposta;
        this.fechamento = fechamento;

    }

    public CotacaoState getCotacao() {
        return cotacao;
    }

    public PropostaState getProposta() {
        return proposta;
    }

    public FechamentoState getFechamento() {
        return fechamento;
    }
}