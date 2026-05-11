package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.CategoriaFrete;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class CategoriaMapper implements Mapper<CategoriaState, CategoriaFrete> {

    @Inject
    public CategoriaMapper() {
    }

    @Override
    public CategoriaFrete mapTo(CategoriaState state) {
        return new CategoriaFrete(state.getId(), state.getDescricao());
    }

    @Override
    public CategoriaState mapFrom(CategoriaFrete f) {
        return new CategoriaState(f.getId(), f.getDescricao(), false);
    }
}
