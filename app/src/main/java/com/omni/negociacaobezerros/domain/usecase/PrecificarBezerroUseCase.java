package com.omni.negociacaobezerros.domain.usecase;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.ValorReferenciaRepository;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;

import java.math.BigDecimal;

import jakarta.inject.Inject;

public final class PrecificarBezerroUseCase {

    private final PrecificacaoBezerroStrategy strategy;
    private final ValorReferenciaRepository valorReferenciaRepository;

    @Inject
    public PrecificarBezerroUseCase(PrecificacaoBezerroStrategy strategy, ValorReferenciaRepository valorReferenciaRepository) {
        this.strategy = strategy;
        this.valorReferenciaRepository = valorReferenciaRepository;
    }

    public PrecificacaoBezerro calcular(BigDecimal peso, Integer quantidade) {
        return strategy.calcular(peso, quantidade, getParametrosByValorReferencia());
    }

    private ParametrosBezerro getParametrosByValorReferencia() {
        return fromReferencia(getValorReferencia());
    }

    private ValorReferencia getValorReferencia() {
        return valorReferenciaRepository.findMaisRecente()
                .orElseThrow(() -> new IllegalStateException("Nenhum valor de referência cadastrado"));
    }

    private ParametrosBezerro fromReferencia(ValorReferencia referencia) {
        return new ParametrosBezerro(
                BigDecimal.valueOf(referencia.getPesoBezerro()),
                BigDecimal.valueOf(referencia.getValorArrobaBoi()),
                BigDecimal.valueOf(referencia.getAgioBezerro())
        );
    }
}
