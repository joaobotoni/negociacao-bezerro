package com.example.myapplication.domain.implementation;

import com.example.myapplication.data.models.ParametrosBezerro;
import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.data.repositories.ValorReferenciaRepository;
import com.example.myapplication.data.source.local.entities.ValorReferencia;
import com.example.myapplication.domain.contract.PrecificacaoBezerroStrategy;

import java.math.BigDecimal;

import jakarta.inject.Inject;

public final class PrecificacaoBezerroImplementation {
    private final PrecificacaoBezerroStrategy strategy;
    private final ValorReferenciaRepository valorReferenciaRepository;
    @Inject
    public PrecificacaoBezerroImplementation(PrecificacaoBezerroStrategy strategy, ValorReferenciaRepository valorReferenciaRepository) {
        this.strategy = strategy;
        this.valorReferenciaRepository = valorReferenciaRepository;
    }

    public PrecificacaoBezerro executar(BigDecimal peso, Integer quantidade) {
        return strategy.calcular(peso, quantidade, parametrosAtuais());
    }

    private ParametrosBezerro parametrosAtuais() {
        ValorReferencia referencia = valorReferenciaRepository.findMaisRecente()
                .orElseThrow(() -> new IllegalStateException("Nenhum valor de referência cadastrado"));
        return new ParametrosBezerro(
                BigDecimal.valueOf(referencia.getValorArrobaBoi()),
                BigDecimal.valueOf(referencia.getAgioBezerro()),
                BigDecimal.valueOf(referencia.getPesoBezerro())
        );
    }
}
