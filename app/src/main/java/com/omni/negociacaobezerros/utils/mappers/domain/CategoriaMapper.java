package com.omni.negociacaobezerros.utils.mappers.domain;

import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;
import com.omni.negociacaobezerros.ui.state.animal.CategoriaState;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

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
