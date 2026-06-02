package com.omni.negociacaobezerros.domain.strategy;

import static com.omni.negociacaobezerros.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_MONETARIA;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;

import java.math.BigDecimal;

import jakarta.inject.Inject;

public final class PrecificacaoBezerroSemFrete implements PrecificacaoBezerroStrategy {
    private final PrecificacaoBezerroRepository repository;
    private final BigDecimal fretePorKg;

    @Inject
    public PrecificacaoBezerroSemFrete(PrecificacaoBezerroRepository repository, BigDecimal fretePorKg) {
        this.repository = repository;
        this.fretePorKg = fretePorKg;
    }

    @Override
    public PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade, ParametrosBezerro parametros) {
        BigDecimal valorPorKg = getValorKg(peso, parametros);
        BigDecimal valorPorCabeca = getValorCabeca(valorPorKg, peso);
        BigDecimal valorTotal = getValorTotal(valorPorCabeca, quantidade);
        return new PrecificacaoBezerro(valorPorKg, valorPorCabeca, valorTotal, quantidade);
    }

    private BigDecimal getValorKg(BigDecimal peso, ParametrosBezerro parametros) {
        return repository.calcularValorPorKg(peso, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg).subtract(fretePorKg);
    }

    private BigDecimal getValorCabeca(BigDecimal valorPorKg, BigDecimal peso) {
        return valorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal getValorTotal(BigDecimal valorPorCabeca, Integer quantidade) {
        return repository.calcularValorTotalLote(valorPorCabeca, quantidade);
    }
}
