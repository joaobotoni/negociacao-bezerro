package com.omni.negociacaobezerros.utils.mappers.domain;


import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.ui.state.frete.TransporteState;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
public class TransporteMapper implements Mapper<List<TransporteState>, List<Transporte>> {
    @Inject
    public TransporteMapper() {
    }
    @Override
    public List<Transporte> mapTo(List<TransporteState> transporteStates) {
        return transporteStates.stream().map(transporteState -> new Transporte(
                        transporteState.getId(),
                        transporteState.getNomeVeiculo(),
                        transporteState.getQuantidade(),
                        transporteState.getCapacidade(),
                        transporteState.getOcupacao()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransporteState> mapFrom(List<Transporte> transportes) {
        return transportes.stream().map(t -> new TransporteState(
                        t.getId(),
                        t.getNomeVeiculo(),
                        t.getQuantidade(),
                        t.getCapacidade(),
                        t.getOcupacao()))
                .collect(Collectors.toList());
    }
}
