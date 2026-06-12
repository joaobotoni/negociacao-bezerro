package com.omni.negociacaobezerros.utils.mapper;

import com.omni.negociacaobezerros.data.models.Rota;
import com.omni.negociacaobezerros.ui.states.RotaUiState;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "inject")
public interface RotaMapper {

    @Mapping(source = "distancia", target = "distanciaKm")
    RotaUiState mapFrom(Rota rota);

    @Mapping(source = "distanciaKm", target = "distancia")
    Rota mapTo(RotaUiState rotaUiState);
}