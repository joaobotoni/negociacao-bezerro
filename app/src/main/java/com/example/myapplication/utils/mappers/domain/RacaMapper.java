package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.Raca;
import com.example.myapplication.ui.state.RacaState;
import com.example.myapplication.utils.mappers.Mapper;

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