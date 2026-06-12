package com.omni.negociacaobezerros.utils.mapper;

import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.ui.states.TransporteUiState;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "inject")
public interface TransporteMapper {
    @Mapping(source = "quantidade", target = "quantidadeVeiculos")
    @Mapping(source = "capacidade", target = "capacidadeCabecas")
    @Mapping(source = "ocupacao", target = "porcentagemOcupada")
    TransporteUiState mapFrom(Transporte transporte);

    @Mapping(source = "quantidadeVeiculos", target = "quantidade")
    @Mapping(source = "capacidadeCabecas", target = "capacidade")
    @Mapping(source = "porcentagemOcupada", target = "ocupacao")
    Transporte mapTo(TransporteUiState uiState);
    List<Transporte> mapTo(List<TransporteUiState> uiStates);
    List<TransporteUiState> mapFrom(List<Transporte> transportes);
}