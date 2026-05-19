package com.omni.negociacaobezerros.data.repositories;

import static com.omni.negociacaobezerros.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.CEM;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_CALCULO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_MONETARIA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Inject;

public class PrecificacaoBezerroRepository {
    private static final BigDecimal PESO_ARROBA_KG = new BigDecimal("30.0");
    private static final BigDecimal ARROBAS_ABATE_ESPERADAS = new BigDecimal("21.00");
    private static final BigDecimal TAXA_FIXA_ABATE = new BigDecimal("69.70");
    private static final BigDecimal IMPOSTO_FUNRURAL = new BigDecimal("0.015");

    @Inject
    public PrecificacaoBezerroRepository() {}

    public BigDecimal calcularValorPorCabeca(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return calcularValorBasePorPeso(pesoKg, precoPorArroba)
                .add(calcularValorTotalAgio(pesoKg, precoPorArroba, percentualAgio, pesoBaseKg))
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    public BigDecimal calcularValorPorKg(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return pesoKg.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : calcularValorPorCabeca(pesoKg, precoPorArroba, percentualAgio, pesoBaseKg)
                .divide(pesoKg, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    }

    public BigDecimal calcularValorTotalLote(BigDecimal valorUnitario, Integer quantidade) {
        return valorUnitario.multiply(new BigDecimal(quantidade))
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularValorTotalAgio(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return estaNoOuAcimaPesoBase(pesoKg, pesoBaseKg)
                ? calcularAgioAcimaPesoBase(pesoKg, precoPorArroba, percentualAgio, pesoBaseKg)
                : calcularAgioAbaixoPesoBase(pesoKg, precoPorArroba, percentualAgio, pesoBaseKg);
    }

    private BigDecimal calcularAgioAcimaPesoBase(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return obterArrobasRestantesParaAbate(pesoKg)
                .multiply(calcularAgioPorArrobaNoPesoBase(pesoKg, precoPorArroba, percentualAgio, pesoBaseKg));
    }

    private BigDecimal calcularAgioAbaixoPesoBase(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        BigDecimal acumulado = BigDecimal.ZERO;
        BigDecimal pesoAtual = pesoKg;
        while (pesoAtual.compareTo(pesoBaseKg) < 0) {
            acumulado = acumulado.add(calcularDiferencaAgioNoPeso(pesoAtual, precoPorArroba, percentualAgio, pesoBaseKg));
            pesoAtual = calcularProximoPesoSuperior(pesoAtual, pesoBaseKg);
        }
        return acumulado.add(calcularAgioAcimaPesoBase(pesoBaseKg, precoPorArroba, percentualAgio, pesoBaseKg));
    }

    private BigDecimal calcularAgioPorArrobaNoPesoBase(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return obterValorReferenciaAgioNoPesoBase(precoPorArroba, percentualAgio, pesoBaseKg)
                .subtract(calcularTaxaPorArrobaRestante(pesoKg, precoPorArroba));
    }

    private BigDecimal calcularDiferencaAgioNoPeso(BigDecimal pesoKg, BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return obterValorReferenciaAgioNoPesoBase(precoPorArroba, percentualAgio, pesoBaseKg)
                .subtract(calcularTaxaPorArrobaRestante(pesoKg, precoPorArroba));
    }

    private BigDecimal obterValorReferenciaAgioNoPesoBase(BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return calcularTaxaPorArrobaRestante(pesoBaseKg, precoPorArroba)
                .add(calcularAgioPorArrobaExatamenteNoPesoBase(precoPorArroba, percentualAgio, pesoBaseKg));
    }

    private BigDecimal calcularAgioPorArrobaExatamenteNoPesoBase(BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return calcularValorAgioNoPesoBase(precoPorArroba, percentualAgio, pesoBaseKg)
                .divide(obterArrobasRestantesParaAbate(pesoBaseKg), ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularValorAgioNoPesoBase(BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return calcularValorPesoBaseComAgio(precoPorArroba, percentualAgio, pesoBaseKg)
                .subtract(converterKgParaArrobas(pesoBaseKg).multiply(precoPorArroba));
    }

    private BigDecimal calcularValorPesoBaseComAgio(BigDecimal precoPorArroba, BigDecimal percentualAgio, BigDecimal pesoBaseKg) {
        return converterKgParaArrobas(pesoBaseKg)
                .multiply(precoPorArroba)
                .divide(obterFatorMultiplicadorAgio(percentualAgio), ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularValorBasePorPeso(BigDecimal pesoKg, BigDecimal precoPorArroba) {
        return converterKgParaArrobas(pesoKg).multiply(precoPorArroba);
    }

    private BigDecimal calcularTaxaPorArrobaRestante(BigDecimal pesoKg, BigDecimal precoPorArroba) {
        BigDecimal restantes = obterArrobasRestantesParaAbate(pesoKg);
        return restantes.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : calcularTotalTaxasAbate(precoPorArroba)
                .divide(restantes, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularTotalTaxasAbate(BigDecimal precoPorArroba) {
        return ARROBAS_ABATE_ESPERADAS.multiply(precoPorArroba).multiply(IMPOSTO_FUNRURAL).add(TAXA_FIXA_ABATE);
    }

    private BigDecimal obterArrobasRestantesParaAbate(BigDecimal pesoKg) {
        return ARROBAS_ABATE_ESPERADAS.subtract(converterKgParaArrobas(pesoKg));
    }

    private BigDecimal converterKgParaArrobas(BigDecimal pesoKg) {
        return pesoKg.divide(PESO_ARROBA_KG, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal obterFatorMultiplicadorAgio(BigDecimal percentualAgio) {
        return CEM.subtract(percentualAgio).divide(CEM, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularProximoPesoSuperior(BigDecimal pesoKg, BigDecimal pesoBaseKg) {
        BigDecimal proximoPeso = arredondarArrobasParaCima(converterKgParaArrobas(pesoKg)).multiply(PESO_ARROBA_KG);
        return limitarPesoAoPesoBase(proximoPeso, pesoBaseKg);
    }

    private BigDecimal arredondarArrobasParaCima(BigDecimal arrobas) {
        BigDecimal arredondado = arrobas.setScale(0, RoundingMode.CEILING);
        return arredondado.compareTo(arrobas) == 0 ? arredondado.add(BigDecimal.ONE) : arredondado;
    }

    private BigDecimal limitarPesoAoPesoBase(BigDecimal pesoKg, BigDecimal pesoBaseKg) {
        return pesoKg.compareTo(pesoBaseKg) > 0 ? pesoBaseKg : pesoKg;
    }

    private boolean estaNoOuAcimaPesoBase(BigDecimal pesoKg, BigDecimal pesoBaseKg) {
        return pesoKg.compareTo(pesoBaseKg) >= 0;
    }
}