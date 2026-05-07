package com.example.myapplication.utils.mappers.domain;



import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.ui.state.SimulacaoUiState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class PrecificacaoBezerroMapper implements Mapper<SimulacaoUiState, PrecificacaoBezerro> {

    @Inject
    public PrecificacaoBezerroMapper() {
    }

    @Override
    public PrecificacaoBezerro mapTo(SimulacaoUiState precificacaoBezerroUiState) {
        return new PrecificacaoBezerro(
                precificacaoBezerroUiState.getValorPorKg(),
                precificacaoBezerroUiState.getValorPorCabeca(),
                precificacaoBezerroUiState.getValorTotal(),
                precificacaoBezerroUiState.getQuantidade()
        );
    }

    @Override
    public SimulacaoUiState mapFrom(PrecificacaoBezerro precificacaoBezerro) {
        return new SimulacaoUiState(
                precificacaoBezerro.getValorTotal(),
                precificacaoBezerro.getValorPorCabeca(),
                precificacaoBezerro.getValorPorKg(),
                precificacaoBezerro.getQuantidade()
        );
    }
}
