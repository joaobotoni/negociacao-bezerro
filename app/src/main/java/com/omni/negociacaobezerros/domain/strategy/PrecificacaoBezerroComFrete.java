package com.omni.negociacaobezerros.domain.strategy;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;

import java.math.BigDecimal;

import jakarta.inject.Inject;

public final class PrecificacaoBezerroComFrete implements PrecificacaoBezerroStrategy {
    private final PrecificacaoBezerroRepository repository;

    @Inject
    public PrecificacaoBezerroComFrete(PrecificacaoBezerroRepository repository) {
        this.repository = repository;
    }

    @Override
    public PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade, ParametrosBezerro parametros) {
        BigDecimal valorPorKg = getValorKg(peso, parametros);
        BigDecimal valorPorCabeca = getValorCabeca(peso, parametros);
        BigDecimal valorTotal = getValorTotal(valorPorCabeca, quantidade);
        return new PrecificacaoBezerro(valorPorKg, valorPorCabeca, valorTotal, quantidade);
    }
    private BigDecimal getValorKg(BigDecimal peso, ParametrosBezerro parametros) {
        return repository.calcularValorPorKg(peso, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg);
    }

    private BigDecimal getValorCabeca(BigDecimal peso, ParametrosBezerro parametros) {
        return repository.calcularValorPorCabeca(peso, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg);
    }

    private BigDecimal getValorTotal(BigDecimal valorPorCabeca, Integer quantidade) {
        return repository.calcularValorTotalLote(valorPorCabeca, quantidade);
    }
}
