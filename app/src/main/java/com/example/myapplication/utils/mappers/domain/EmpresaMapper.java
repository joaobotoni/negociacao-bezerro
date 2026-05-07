package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.Empresa;
import com.example.myapplication.ui.state.EmpresaUiState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class EmpresaMapper implements Mapper<EmpresaUiState, Empresa> {

    @Inject
    public EmpresaMapper() {}

    @Override
    public Empresa mapTo(EmpresaUiState state) {
        return new Empresa(state.getId(), state.getNome());
    }

    @Override
    public EmpresaUiState mapFrom(Empresa e) {
        return new EmpresaUiState(e.getIdEmpresa(), e.getNome(), false);
    }
}
