package com.omni.negociacaobezerros.utils.mappers.domain;


import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

import javax.inject.Inject;

public class PrecificacaoBezerroMapper implements Mapper<CotacaoState, PrecificacaoBezerro> {

    @Inject
    public PrecificacaoBezerroMapper() {
    }

    @Override
    public PrecificacaoBezerro mapTo(CotacaoState cotacaoState) {
        return new PrecificacaoBezerro(
                cotacaoState.getValorPorKg(),
                cotacaoState.getValorPorCabeca(),
                cotacaoState.getValorTotal(),
                cotacaoState.getQuantidade()
        );
    }

    @Override
    public CotacaoState mapFrom(PrecificacaoBezerro precificacaoBezerro) {
        return new CotacaoState(
                precificacaoBezerro.getValorPorKg(),
                precificacaoBezerro.getValorPorCabeca(),
                precificacaoBezerro.getQuantidade(),
                precificacaoBezerro.getValorTotal()
        );
    }
}
