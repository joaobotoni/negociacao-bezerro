package com.omni.negociacaobezerros.utils.mapper;

import com.omni.negociacaobezerros.data.source.local.entities.Empresa;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "inject")
public interface EmpresaMapper {

    @Mapping(source = "idEmpresa", target = "id")
    @Mapping(target = "selecionada", constant = "false")
    EmpresaUiState mapFrom(Empresa empresa);

    @Mapping(source = "id", target = "idEmpresa")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "localizacao", source = "localizacao")
    Empresa mapTo(EmpresaUiState empresaUiState);
}
