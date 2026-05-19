package com.omni.negociacaobezerros.utils.mappers.domain;

import com.omni.negociacaobezerros.data.source.local.entities.Corretor;
import com.omni.negociacaobezerros.ui.state.empresa.CorretorState;
import com.omni.negociacaobezerros.utils.mappers.Mapper;

import java.math.BigDecimal;

import javax.inject.Inject;

public class CorretorMapper implements Mapper<CorretorState, Corretor> {

    @Inject
    public CorretorMapper() {
    }

    @Override
    public Corretor mapTo(CorretorState state) {
        return new Corretor(state.getId(), state.getNome(), state.getComissao().doubleValue(), state.getTipoComissao());
    }

    @Override
    public CorretorState mapFrom(Corretor c) {
        return new CorretorState(c.getIdCorretor(), c.getNome(), BigDecimal.valueOf(c.getComissao()), c.getTipoComissao(), false);
    }
}
