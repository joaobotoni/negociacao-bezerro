package com.omni.negociacaobezerros.utils.mapper;

import com.omni.negociacaobezerros.data.source.local.entities.Corretor;
import com.omni.negociacaobezerros.ui.states.CorretorUiState;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "inject")
public interface CorretorMapper {

    @Mapping(source = "idCorretor", target = "id")
    @Mapping(target = "selecionado", constant = "false")
    CorretorUiState mapFrom(Corretor corretor);

    @Mapping(source = "id", target = "idCorretor")
    Corretor mapTo(CorretorUiState uiState);
}