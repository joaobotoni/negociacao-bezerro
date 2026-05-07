package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.Raca;
import com.example.myapplication.ui.state.RacaUiState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class RacaMapper implements Mapper<RacaUiState, Raca> {

    @Inject
    public RacaMapper() {
    }

    @Override
    public Raca mapTo(RacaUiState state) {
        return new Raca(state.getId(), state.getDescricao(), null);
    }

    @Override
    public RacaUiState mapFrom(Raca r) {
        return new RacaUiState(r.getIdRaca(), r.getDescricao(), false);
    }
}