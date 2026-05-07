package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.Corretor;
import com.example.myapplication.ui.state.CorretorUiState;
import com.example.myapplication.utils.mappers.Mapper;

import java.math.BigDecimal;

import javax.inject.Inject;

public class CorretorMapper implements Mapper<CorretorUiState, Corretor> {

    @Inject
    public CorretorMapper() {
    }

    @Override
    public Corretor mapTo(CorretorUiState state) {
        return new Corretor(state.getId(), state.getNome(), state.getComissao().doubleValue(), state.getTipoComissao());
    }

    @Override
    public CorretorUiState mapFrom(Corretor c) {
        return new CorretorUiState(c.getIdCorretor(), c.getNome(), BigDecimal.valueOf(c.getComissao()), c.getTipoComissao(), false);
    }
}
