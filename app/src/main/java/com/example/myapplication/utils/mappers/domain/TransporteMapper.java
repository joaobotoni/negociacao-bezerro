package com.example.myapplication.utils.mappers.domain;


import com.example.myapplication.data.models.Transporte;
import com.example.myapplication.ui.state.TransporteUiState;
import com.example.myapplication.utils.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
public class TransporteMapper implements Mapper<List<TransporteUiState>, List<Transporte>> {
    @Inject
    public TransporteMapper() {
    }
    @Override
    public List<Transporte> mapTo(List<TransporteUiState> transporteUiStates) {
        return transporteUiStates.stream().map(transporteUiState -> new Transporte(
                        transporteUiState.getId(),
                        transporteUiState.getNomeVeiculo(),
                        transporteUiState.getQuantidade(),
                        transporteUiState.getCapacidade(),
                        transporteUiState.getOcupacao()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransporteUiState> mapFrom(List<Transporte> transportes) {
        return transportes.stream().map(t -> new TransporteUiState(
                        t.getId(),
                        t.getNomeVeiculo(),
                        t.getQuantidade(),
                        t.getCapacidade(),
                        t.getOcupacao()))
                .collect(Collectors.toList());
    }
}
