package com.omni.negociacaobezerros.utils.mapper;

import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;
import com.omni.negociacaobezerros.ui.states.CategoriaUiState;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "inject")
public interface CategoriaMapper {

    @Mapping(source = "descricao", target = "opcao")
    @Mapping(target = "selecionada", constant = "false")
    CategoriaUiState mapFrom(CategoriaFrete categoriaFrete);

    @Mapping(source = "opcao", target = "descricao")
    CategoriaFrete mapTo(CategoriaUiState categoriaUiState);
}