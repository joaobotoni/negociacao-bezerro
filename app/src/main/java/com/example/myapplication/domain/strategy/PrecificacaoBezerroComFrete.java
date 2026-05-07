package com.example.myapplication.domain.strategy;

import com.example.myapplication.data.models.ParametrosBezerro;
import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.data.repositories.PrecificacaoBezerroRepository;
import com.example.myapplication.domain.contract.PrecificacaoBezerroStrategy;

import java.math.BigDecimal;

import jakarta.inject.Inject;

public class PrecificacaoBezerroComFrete implements PrecificacaoBezerroStrategy {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    @Inject
    public PrecificacaoBezerroComFrete(PrecificacaoBezerroRepository precificacaoBezerroRepository) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
    }
    @Override
    public PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade, ParametrosBezerro parametros) {
        BigDecimal valorPorKg = precificacaoBezerroRepository.calcularValorPorKg(peso, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg);
        BigDecimal valorPorCabeca = precificacaoBezerroRepository.calcularValorPorCabeca(peso, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg);
        BigDecimal valorTotal = precificacaoBezerroRepository.calcularValorTotalLote(valorPorCabeca, quantidade);
        return new PrecificacaoBezerro(valorPorKg, valorPorCabeca, valorTotal, quantidade);
    }
}
