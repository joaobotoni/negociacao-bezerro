package com.omni.negociacaobezerros.utils.mappers.domain;

import com.omni.negociacaobezerros.data.source.local.entities.Empresa;
import com.omni.negociacaobezerros.ui.state.empresa.EmpresaState;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

import javax.inject.Inject;

public class EmpresaMapper implements Mapper<EmpresaState, Empresa> {

    @Inject
    public EmpresaMapper() {}

    @Override
    public Empresa mapTo(EmpresaState state) {
        return new Empresa(state.getId(), state.getNome());
    }

    @Override
    public EmpresaState mapFrom(Empresa e) {
        return new EmpresaState(e.getIdEmpresa(), e.getNome(), false);
    }
}
