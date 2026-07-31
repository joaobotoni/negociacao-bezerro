package com.omni.negociacaobezerros.ui.viewmodels;

import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.format.Decimals.CEM;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_CALCULO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_MONETARIA;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.ValorReferenciaRepository;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroComFrete;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroComFreteEComissao;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroSemFrete;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.PrecificacaoBezerroUiState;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NegociacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;

    private final MutableLiveData<BigDecimal> peso = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> quantidade = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> fretePorKg = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> comissaoPorKg = new MutableLiveData<>(null);

    private final MutableLiveData<BigDecimal> cotacaoReferenciaPorKg = new MutableLiveData<>(null);
    private final MutableLiveData<PrecificacaoBezerroUiState> propostaState = new MutableLiveData<>(null);
    private final MutableLiveData<PrecificacaoBezerroUiState> fechamentoState = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> variacaoPercentual = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> botaoAtivo = new MutableLiveData<>(false);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public NegociacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository,
                                ValorReferenciaRepository valorReferenciaRepository,
                                TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<PrecificacaoBezerroUiState> getPropostaState() { return propostaState; }
    public LiveData<PrecificacaoBezerroUiState> getFechamentoState() { return fechamentoState; }
    public LiveData<BigDecimal> getVariacaoPercentual() { return variacaoPercentual; }
    public LiveData<Boolean> getBotaoAtivo() { return botaoAtivo; }
    public LiveData<Throwable> getErro() { return erro; }

    public void setPeso(BigDecimal pesoInformado) {
        peso.setValue(pesoInformado);
        recalcularSeProntoMinimo();
    }

    public void setQuantidade(Integer quantidadeInformada) {
        quantidade.setValue(quantidadeInformada);
        recalcularSeProntoMinimo();
    }

    public void setFrete(BigDecimal fretePorKgInformado) {
        fretePorKg.setValue(fretePorKgInformado);
        recalcularSeProntoMinimo();
    }

    public void setComissao(BigDecimal comissaoPorKgInformada) {
        comissaoPorKg.setValue(comissaoPorKgInformada);
        recalcularSeProntoMinimo();
    }

    public void recalcularPorKgManual(BigDecimal valorKgInformado) {
        if (!isProntoMinimo()) return;
        BigDecimal valorCabeca = arredondarMonetario(valorKgInformado.multiply(peso.getValue()));
        publicarOverride(valorKgInformado, valorCabeca);
    }

    public void recalcularPorCabecaManual(BigDecimal valorCabecaInformado) {
        if (!isProntoMinimo() || isPesoZero(peso.getValue())) return;
        BigDecimal valorKg = valorCabecaInformado.divide(peso.getValue(), ESCALA_CALCULO, ARREDONDAMENTO_PADRAO);
        publicarOverride(valorKg, valorCabecaInformado);
    }

    public void limpar() {
        peso.setValue(null);
        quantidade.setValue(null);
        fretePorKg.setValue(null);
        comissaoPorKg.setValue(null);
        cotacaoReferenciaPorKg.setValue(null);
        propostaState.setValue(null);
        fechamentoState.setValue(null);
        variacaoPercentual.setValue(null);
        botaoAtivo.setValue(false);
        erro.setValue(null);
    }

    private void recalcularSeProntoMinimo() {
        if (!isProntoMinimo()) return;
        recalcular(peso.getValue(), quantidade.getValue(), orZero(fretePorKg.getValue()), orZero(comissaoPorKg.getValue()));
    }

    private boolean isProntoMinimo() {
        return peso.getValue() != null && quantidade.getValue() != null;
    }

    private void recalcular(BigDecimal pesoAtual, Integer quantidadeAtual, BigDecimal fretePorKgAtual, BigDecimal comissaoPorKgAtual) {
        taskHelper.execute(
                this::carregarParametros,
                parametros -> postResultado(parametros, pesoAtual, quantidadeAtual, fretePorKgAtual, comissaoPorKgAtual),
                erro::setValue);
    }

    private ParametrosBezerro carregarParametros() {
        ValorReferencia referencia = valorReferenciaRepository.findMaisRecente()
                .orElseThrow(() -> new IllegalStateException("Nenhum valor de referência cadastrado"));
        return new ParametrosBezerro(
                Numbers.parseDecimal(referencia.getPesoBezerro()),
                Numbers.parseDecimal(referencia.getValorArrobaBoi()),
                Numbers.parseDecimal(referencia.getAgioBezerro()));
    }

    private void postResultado(ParametrosBezerro parametros, BigDecimal pesoAtual, Integer quantidadeAtual,
                                BigDecimal fretePorKgAtual, BigDecimal comissaoPorKgAtual) {
        BigDecimal cotacaoPorKg = precificacaoBezerroRepository.calcularValorPorKg(
                pesoAtual, parametros.precoPorArroba, parametros.percentualAgio, parametros.pesoBaseKg);

        PrecificacaoBezerroStrategy propostaStrategy = isFreteInformado(fretePorKgAtual)
                ? new PrecificacaoBezerroSemFrete(precificacaoBezerroRepository, fretePorKgAtual)
                : new PrecificacaoBezerroComFrete(precificacaoBezerroRepository);
        PrecificacaoBezerro proposta = propostaStrategy.calcular(pesoAtual, quantidadeAtual, parametros);

        PrecificacaoBezerro fechamento = new PrecificacaoBezerroComFreteEComissao(precificacaoBezerroRepository, comissaoPorKgAtual)
                .calcular(pesoAtual, quantidadeAtual, parametros);

        BigDecimal variacao = calcularVariacaoPercentual(cotacaoPorKg, fechamento.getValorPorKg());

        cotacaoReferenciaPorKg.setValue(cotacaoPorKg);
        propostaState.setValue(new PrecificacaoBezerroUiState(proposta.getValorPorKg(), proposta.getValorPorCabeca()));
        fechamentoState.setValue(new PrecificacaoBezerroUiState(fechamento.getValorPorKg(), fechamento.getValorPorCabeca()));
        variacaoPercentual.setValue(variacao);
        botaoAtivo.setValue(true);
    }

    private void publicarOverride(BigDecimal valorKg, BigDecimal valorCabeca) {
        propostaState.setValue(new PrecificacaoBezerroUiState(valorKg, valorCabeca));
        BigDecimal comissaoAtual = orZero(comissaoPorKg.getValue());
        BigDecimal valorKgFechamento = valorKg.add(comissaoAtual);
        BigDecimal valorCabecaFechamento = arredondarMonetario(valorKgFechamento.multiply(peso.getValue()));
        fechamentoState.setValue(new PrecificacaoBezerroUiState(valorKgFechamento, valorCabecaFechamento));
        if (cotacaoReferenciaPorKg.getValue() != null) {
            variacaoPercentual.setValue(calcularVariacaoPercentual(cotacaoReferenciaPorKg.getValue(), valorKgFechamento));
        }
        botaoAtivo.setValue(true);
    }

    private BigDecimal calcularVariacaoPercentual(BigDecimal cotacaoPorKg, BigDecimal valorPorKg) {
        if (cotacaoPorKg == null || cotacaoPorKg.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valorPorKg.subtract(cotacaoPorKg)
                .divide(cotacaoPorKg, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .multiply(CEM)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private boolean isFreteInformado(BigDecimal fretePorKgAtual) {
        return fretePorKgAtual.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isPesoZero(BigDecimal pesoAtual) {
        return pesoAtual.compareTo(BigDecimal.ZERO) == 0;
    }

    private BigDecimal arredondarMonetario(BigDecimal valor) {
        return valor.setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal orZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
