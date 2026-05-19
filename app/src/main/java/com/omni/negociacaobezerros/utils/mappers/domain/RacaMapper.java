package com.omni.negociacaobezerros.utils.mappers.domain;

import com.omni.negociacaobezerros.data.source.local.entities.Raca;
import com.omni.negociacaobezerros.ui.state.animal.RacaState;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

import javax.inject.Inject;

public class RacaMapper implements Mapper<RacaState, Raca> {

    @Inject
    public RacaMapper() {
    }

    @Override
    public Raca mapTo(RacaState state) {
        return new Raca(state.getId(), state.getDescricao(), null);
    }

    @Override
    public RacaState mapFrom(Raca r) {
        return new RacaState(r.getIdRaca(), r.getDescricao(), false);
    }
}