package com.example.myapplication.utils.mappers.domain;



import com.example.myapplication.data.models.Rota;
import com.example.myapplication.ui.state.RotaUiState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class RotaMapper implements Mapper<RotaUiState, Rota> {

    @Inject
    public RotaMapper() {
    }

    @Override
    public Rota mapTo(RotaUiState rotaUiState) {
        return new Rota(
                rotaUiState.getCidadeOrigem(),
                rotaUiState.getEstadoOrigem(),
                rotaUiState.getCidadeDestino(),
                rotaUiState.getEstadoDestino(),
                rotaUiState.getDistancia());
    }

    @Override
    public RotaUiState mapFrom(Rota rota) {
        return new RotaUiState(
                rota.getCidadeOrigem(),
                rota.getEstadoOrigem(),
                rota.getCidadeDestino(),
                rota.getEstadoDestino(),
                rota.getDistancia());
    }
}
