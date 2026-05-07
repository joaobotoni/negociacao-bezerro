package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.CategoriaFrete;
import com.example.myapplication.ui.state.CategoriaUiState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class CategoriaMapper implements Mapper<CategoriaUiState, CategoriaFrete> {

    @Inject
    public CategoriaMapper() {
    }

    @Override
    public CategoriaFrete mapTo(CategoriaUiState state) {
        return new CategoriaFrete(state.getId(), state.getDescricao());
    }

    @Override
    public CategoriaUiState mapFrom(CategoriaFrete f) {
        return new CategoriaUiState(f.getId(), f.getDescricao(), false);
    }
}
