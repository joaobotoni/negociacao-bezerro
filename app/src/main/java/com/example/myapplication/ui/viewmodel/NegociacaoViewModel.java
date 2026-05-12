package com.example.myapplication.ui.viewmodel;

import static com.example.myapplication.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.example.myapplication.utils.DecimalUtil.CEM;
import static com.example.myapplication.utils.DecimalUtil.ESCALA_CALCULO;
import static com.example.myapplication.utils.DecimalUtil.ESCALA_MONETARIA;

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
import com.example.myapplication.ui.state.negociacao.NegociacaoState;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.state.negociacao.FechamentoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class NegociacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;
    private final MutableLiveData<NegociacaoState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Double> variacao = new MutableLiveData<>(0.0);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private CotacaoState cotacao;
    private PropostaState proposta;
    private FechamentoState fechamento;

    @Inject
    public NegociacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository,
                               ValorReferenciaRepository valorReferenciaRepository,
                               TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<NegociacaoState> getState() { return state; }
    public LiveData<Double> getVariacao() { return variacao; }
    public LiveData<Throwable> getError() { return error; }

    public void processarCotacao(BigDecimal peso, Integer quantidade) {
        taskHelper.execute(() -> calcularCotacao(peso, quantidade), state::postValue, error::postValue);
    }

    public void processarProposta(BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, FreteState freteState) {
        taskHelper.execute(() -> calcularProposta(peso, quantidade, freteTotalLote, freteState), state::postValue, error::postValue);
    }

    public void processarFechamento(BigDecimal peso, Integer quantidade, BigDecimal comissaoTotal) {
        taskHelper.execute(() -> calcularFechamento(peso, quantidade, comissaoTotal), negociacaoState -> {
            state.postValue(negociacaoState);
            variacao.postValue(calcularVariacaoPercentual(
                    negociacaoState.getCotacao().getValorTotal(),
                    negociacaoState.getFechamento().getValorTotal()));
        }, error::postValue);
    }

    public void recalcularPropostaPorKg(BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade) {
        taskHelper.execute(() -> calcularPropostaPorKg(novoValorPorKg, peso, quantidade), state::postValue, error::postValue);
    }

    public void recalcularPropostaPorCabeca(BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade) {
        taskHelper.execute(() -> calcularPropostaPorCabeca(novoValorPorCabeca, peso, quantidade), state::postValue, error::postValue);
    }

    private NegociacaoState calcularCotacao(BigDecimal peso, Integer quantidade) {
        PrecificacaoBezerro precificacao = precificarBezerroComFrete(peso, quantidade);
        cotacao = new CotacaoState(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), precificacao.getValorTotal());
        return build();
    }

    private NegociacaoState calcularProposta(BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, FreteState freteState) {
        BigDecimal fretePorKg = converterFreteTotalParaPorKg(freteTotalLote, peso, quantidade);
        PrecificacaoBezerro precificacao = precificarBezerroSemFrete(peso, quantidade, fretePorKg);
        proposta = new PropostaState(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), precificacao.getValorTotal(), fretePorKg, freteState);
        return build();
    }

    private NegociacaoState calcularFechamento(BigDecimal peso, Integer quantidade, BigDecimal comissaoTotal) {
        BigDecimal comissaoPorKg = converterComissaoTotalParaPorKg(comissaoTotal, peso);
        PrecificacaoBezerro precificacao = precificarBezerroComFreteEComissao(peso, quantidade, comissaoPorKg);
        fechamento = new FechamentoState(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), precificacao.getValorTotal(), comissaoPorKg);
        return build();
    }

    private NegociacaoState calcularPropostaPorKg(BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade) {
        BigDecimal novoValorPorCabeca = novoValorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal novoValorTotal = novoValorPorCabeca.multiply(BigDecimal.valueOf(quantidade)).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        proposta = new PropostaState(novoValorPorKg, novoValorPorCabeca, novoValorTotal, proposta.getFretePorKg(), proposta.getFreteState());
        return recalcularFechamentoSobreProposta(novoValorPorKg, peso, quantidade);
    }

    private NegociacaoState calcularPropostaPorCabeca(BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade) {
        BigDecimal novoValorPorKg = novoValorPorCabeca.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal novoValorTotal = novoValorPorCabeca.multiply(BigDecimal.valueOf(quantidade)).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        proposta = new PropostaState(novoValorPorKg, novoValorPorCabeca, novoValorTotal, proposta.getFretePorKg(), proposta.getFreteState());
        return recalcularFechamentoSobreProposta(novoValorPorKg, peso, quantidade);
    }

    private NegociacaoState recalcularFechamentoSobreProposta(BigDecimal valorPorKgProposta, BigDecimal peso, Integer quantidade) {
        if (fechamento == null || fechamento.getComissaoPorKg() == null) return build();
        BigDecimal comissaoPorKg = fechamento.getComissaoPorKg();
        BigDecimal novoValorPorKg = valorPorKgProposta.add(comissaoPorKg).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal novoValorPorCabeca = novoValorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal novoValorTotal = novoValorPorCabeca.multiply(BigDecimal.valueOf(quantidade)).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        fechamento = new FechamentoState(novoValorPorKg, novoValorPorCabeca, novoValorTotal, comissaoPorKg);
        return build();
    }

    private NegociacaoState build() {
        return new NegociacaoState(cotacao, proposta, fechamento);
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
        return valorFinal.subtract(valorPedido).divide(valorPedido, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).multiply(CEM)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO).doubleValue();
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

    public void limpar() {
        proposta = new PropostaState(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, FreteState.NAO_SELECIONADO);
        fechamento = new FechamentoState(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        state.postValue(build());
    }

    public void limparVariacao() {
        variacao.postValue(0.0);
    }
}