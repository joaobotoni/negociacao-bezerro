package com.omni.negociacaobezerros.domain.implementation;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.ValorReferenciaRepository;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;

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
