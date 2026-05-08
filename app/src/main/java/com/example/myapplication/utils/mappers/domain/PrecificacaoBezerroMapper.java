package com.example.myapplication.utils.mappers.domain;



import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.ui.state.SimulacaoState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class PrecificacaoBezerroMapper implements Mapper<SimulacaoState, PrecificacaoBezerro> {

    @Inject
    public PrecificacaoBezerroMapper() {
    }

    @Override
    public PrecificacaoBezerro mapTo(SimulacaoState precificacaoBezerroUiState) {
        return new PrecificacaoBezerro(
                precificacaoBezerroUiState.getValorPorKg(),
                precificacaoBezerroUiState.getValorPorCabeca(),
                precificacaoBezerroUiState.getValorTotal(),
                precificacaoBezerroUiState.getQuantidade()
        );
    }

    @Override
    public SimulacaoState mapFrom(PrecificacaoBezerro precificacaoBezerro) {
        return new SimulacaoState(
                precificacaoBezerro.getValorTotal(),
                precificacaoBezerro.getValorPorCabeca(),
                precificacaoBezerro.getValorPorKg(),
                precificacaoBezerro.getQuantidade()
        );
    }
}
