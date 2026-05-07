package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.models.PrecificacaoFrete;
import com.example.myapplication.ui.state.PrecificacaoFreteUiState;
import com.example.myapplication.utils.mappers.Mapper;

import jakarta.inject.Inject;

public class PrecificacaoFreteMapper implements Mapper<PrecificacaoFreteUiState, PrecificacaoFrete> {

    @Inject
    public PrecificacaoFreteMapper() {
    }

    @Override
    public PrecificacaoFrete mapTo(PrecificacaoFreteUiState precificacaoFreteUiState) {
        return new PrecificacaoFrete(precificacaoFreteUiState.getValorTotal(), precificacaoFreteUiState.getValorParcial());
    }

    @Override
    public PrecificacaoFreteUiState mapFrom(PrecificacaoFrete o) {
        return new PrecificacaoFreteUiState(o.getValorTotal(), o.getValorParcial());
    }
}
