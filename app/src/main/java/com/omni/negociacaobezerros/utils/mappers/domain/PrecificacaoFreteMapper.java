package com.omni.negociacaobezerros.utils.mappers.domain;

import com.omni.negociacaobezerros.data.models.PrecificacaoFrete;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.ui.state.frete.StatusFrete;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

import jakarta.inject.Inject;

public class PrecificacaoFreteMapper implements Mapper<FreteState, PrecificacaoFrete> {

    @Inject
    public PrecificacaoFreteMapper() {
    }

    @Override
    public PrecificacaoFrete mapTo(FreteState precificacaoFreteUiState) {
        return new PrecificacaoFrete(precificacaoFreteUiState.getValorTotal(), precificacaoFreteUiState.getValorParcial());
    }

    @Override
    public FreteState mapFrom(PrecificacaoFrete o) {
        return new FreteState(o.getValorTotal(), o.getValorParcial(), StatusFrete.SIMULADO);
    }
}
