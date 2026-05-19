package com.omni.negociacaobezerros.domain.contract;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;

import java.math.BigDecimal;

public interface PrecificacaoBezerroStrategy {
    PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade, ParametrosBezerro parametros);
}
