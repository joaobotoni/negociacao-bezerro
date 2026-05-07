package com.example.myapplication.domain.contract;

import com.example.myapplication.data.models.ParametrosBezerro;
import com.example.myapplication.data.models.PrecificacaoBezerro;

import java.math.BigDecimal;

public interface PrecificacaoBezerroStrategy {
    PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade, ParametrosBezerro parametros);
}
