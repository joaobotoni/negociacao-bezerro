package com.omni.negociacaobezerros.domain.strategy;

import static com.omni.negociacaobezerros.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_MONETARIA;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;

import java.math.BigDecimal;

import jakarta.inject.Inject;

public class PrecificacaoBezerroSemFrete implements PrecificacaoBezerroStrategy {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final BigDecimal fretePorKg;
    @Inject
    public PrecificacaoBezerroSemFrete(PrecificacaoBezerroRepository precificacaoBezerroRepository, BigDecimal fretePorKg) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.fretePorKg = fretePorKg;
    }
    @Override
    public PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade, ParametrosBezerro parametros) {
        BigDecimal valorPorKg = precificacaoBezerroRepository.calcularValorPorKg(peso, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg).subtract(fretePorKg);
        BigDecimal valorPorCabeca = valorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal valorTotal = precificacaoBezerroRepository.calcularValorTotalLote(valorPorCabeca, quantidade);
        return new PrecificacaoBezerro(valorPorKg, valorPorCabeca, valorTotal, quantidade);
    }
}
