package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.models.PrecificacaoFrete;
import com.example.myapplication.ui.state.PrecificacaoFreteState;
import com.example.myapplication.utils.mappers.Mapper;

import jakarta.inject.Inject;

public class PrecificacaoFreteMapper implements Mapper<PrecificacaoFreteState, PrecificacaoFrete> {

    @Inject
    public PrecificacaoFreteMapper() {
    }

    @Override
    public PrecificacaoFrete mapTo(PrecificacaoFreteState precificacaoFreteUiState) {
        return new PrecificacaoFrete(precificacaoFreteUiState.getValorTotal(), precificacaoFreteUiState.getValorParcial());
    }

    @Override
    public PrecificacaoFreteState mapFrom(PrecificacaoFrete o) {
        return new PrecificacaoFreteState(o.getValorTotal(), o.getValorParcial());
    }
}
