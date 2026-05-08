package com.example.myapplication.ui.viewmodel;

import static com.example.myapplication.utils.BigDecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.example.myapplication.utils.BigDecimalUtil.CEM;
import static com.example.myapplication.utils.BigDecimalUtil.ESCALA_CALCULO;
import static com.example.myapplication.utils.BigDecimalUtil.ESCALA_MONETARIA;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.data.repositories.PrecificacaoBezerroRepository;
import com.example.myapplication.data.repositories.ValorReferenciaRepository;
import com.example.myapplication.domain.implementation.PrecificacaoBezerroImplementation;
import com.example.myapplication.domain.strategy.PrecificacaoBezerroComFrete;
import com.example.myapplication.domain.strategy.PrecificacaoBezerroComFreteEComissao;
import com.example.myapplication.domain.strategy.PrecificacaoBezerroSemFrete;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.FreteState;
import com.example.myapplication.ui.state.NegociacaoUiState;
import com.example.myapplication.ui.state.negociacao.Cotacao;
import com.example.myapplication.ui.state.negociacao.Fechamento;
import com.example.myapplication.ui.state.negociacao.NegociacaoUiStateBuilder;
import com.example.myapplication.ui.state.negociacao.Proposta;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class NegociacaoViewModel extends ViewModel {

    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;
    private final MutableLiveData<NegociacaoUiState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final NegociacaoUiStateBuilder builder = new NegociacaoUiStateBuilder();

    @Inject
    public NegociacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository,
                               ValorReferenciaRepository valorReferenciaRepository,
                               TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<NegociacaoUiState> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void processarCotacao(BigDecimal peso, Integer quantidade) {
        taskHelper.execute(() -> calcularCotacao(peso, quantidade), state::postValue, error::postValue);
    }

    public void processarProposta(BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, FreteState freteState) {
        taskHelper.execute(() -> calcularProposta(peso, quantidade, freteTotalLote, freteState), state::postValue, error::postValue);
    }

    public void processarFechamento(BigDecimal peso, Integer quantidade, BigDecimal comissaoTotal, BigDecimal valorKgCotado) {
        taskHelper.execute(() -> calcularFechamento(peso, quantidade, comissaoTotal, valorKgCotado), state::postValue, error::postValue);
    }

    public void limpar() {
        builder.setProposta(null);
        builder.setFechamento(null);
        state.postValue(builder.build());
    }

    private NegociacaoUiState calcularCotacao(BigDecimal peso, Integer quantidade) {
        PrecificacaoBezerro precificacao = precificarBezerroComFrete(peso, quantidade);
        builder.setCotacao(new Cotacao(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), precificacao.getValorTotal()));
        return builder.build();
    }

    private NegociacaoUiState calcularProposta(BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, FreteState freteState) {
        BigDecimal fretePorKg = converterFreteTotalParaPorKg(freteTotalLote, peso, quantidade);
        PrecificacaoBezerro precificacao = precificarBezerroSemFrete(peso, quantidade, fretePorKg);
        builder.setProposta(new Proposta(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), precificacao.getValorTotal(), fretePorKg, freteState));
        return builder.build();
    }

    private NegociacaoUiState calcularFechamento(BigDecimal peso, Integer quantidade, BigDecimal comissaoTotal, BigDecimal valorKgCotado) {
        BigDecimal comissaoPorKg = converterComissaoTotalParaPorKg(comissaoTotal, peso);
        PrecificacaoBezerro precificacao = precificarBezerroComFreteEComissao(peso, quantidade, comissaoPorKg);
        double variacao = calcularVariacaoPercentual(valorKgCotado, precificacao.getValorPorKg());
        builder.setFechamento(new Fechamento(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), precificacao.getValorTotal(), comissaoPorKg, variacao));
        return builder.build();
    }

    private BigDecimal converterFreteTotalParaPorKg(BigDecimal freteTotalLote, BigDecimal peso, Integer quantidade) {
        BigDecimal pesoTotal = peso.multiply(BigDecimal.valueOf(quantidade));
        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return freteTotalLote.divide(pesoTotal, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal converterComissaoTotalParaPorKg(BigDecimal comissaoTotal, BigDecimal peso) {
        if (peso.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return comissaoTotal.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private PrecificacaoBezerro precificarBezerroComFrete(BigDecimal peso, Integer quantidade) {
        return new PrecificacaoBezerroImplementation(
                new PrecificacaoBezerroComFrete(precificacaoBezerroRepository),
                valorReferenciaRepository).executar(peso, quantidade);
    }

    private PrecificacaoBezerro precificarBezerroSemFrete(BigDecimal peso, Integer quantidade, BigDecimal fretePorKg) {
        return new PrecificacaoBezerroImplementation(
                new PrecificacaoBezerroSemFrete(precificacaoBezerroRepository, fretePorKg),
                valorReferenciaRepository).executar(peso, quantidade);
    }

    private PrecificacaoBezerro precificarBezerroComFreteEComissao(BigDecimal peso, Integer quantidade, BigDecimal comissaoPorKg) {
        return new PrecificacaoBezerroImplementation(
                new PrecificacaoBezerroComFreteEComissao(precificacaoBezerroRepository, comissaoPorKg),
                valorReferenciaRepository).executar(peso, quantidade);
    }

    private double calcularVariacaoPercentual(BigDecimal valorPedido, BigDecimal valorFinal) {
        return valorFinal.subtract(valorPedido)
                .divide(valorPedido, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .multiply(CEM)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO)
                .doubleValue();
    }
}