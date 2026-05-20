package com.omni.negociacaobezerros.ui.viewmodel;

import static com.omni.negociacaobezerros.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.CEM;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_CALCULO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_MONETARIA;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.data.repositories.ValorReferenciaRepository;
import com.omni.negociacaobezerros.domain.contract.PrecificacaoBezerroStrategy;
import com.omni.negociacaobezerros.domain.implementation.PrecificacaoBezerroImplementation;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroComFreteEComissao;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroSemFrete;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.frete.StatusFrete;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;
import com.omni.negociacaobezerros.ui.state.negociacao.FechamentoState;
import com.omni.negociacaobezerros.ui.state.negociacao.NegociacaoState;
import com.omni.negociacaobezerros.ui.state.negociacao.PropostaState;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class NegociacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;
    private final MutableLiveData<NegociacaoState> state = new MutableLiveData<>(null);
    public final LiveData<PropostaState> proposta = Transformations.map(state, s -> s != null ? s.getProposta() : null);
    public final LiveData<FechamentoState> fechamento = Transformations.map(state, s -> s != null ? s.getFechamento() : null);
    public final LiveData<Double> variacao = Transformations.map(state, this::extrairVariacao);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    @Inject
    public NegociacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository, ValorReferenciaRepository valorReferenciaRepository, TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<NegociacaoState> getState() { return state; }

    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<PropostaState> getProposta() { return proposta; }
    public LiveData<FechamentoState> getFechamento() { return fechamento; }
    public LiveData<Double> getVariacao() { return variacao; }
    public void processarNegociacao(CotacaoState cotacao, BigDecimal peso, Integer quantidade,
                                    BigDecimal freteTotalLote, StatusFrete statusFrete, BigDecimal comissaoTotal) {
        taskHelper.execute(
                () -> criarNegociacao(cotacao, peso, quantidade, freteTotalLote, statusFrete, comissaoTotal),
                state::postValue,
                erro::setValue
        );
    }

    public void recalcularPropostaPorKg(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        try {
            state.setValue(atualizarPropostaPorKg(cotacao, fechamento, novoValorPorKg, peso, quantidade, fretePorKg, statusFrete));
        } catch (Exception e) {
            erro.setValue(e);
        }
    }

    public void recalcularPropostaPorCabeca(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        try {
            state.setValue(atualizarPropostaPorCabeca(cotacao, fechamento, novoValorPorCabeca, peso, quantidade, fretePorKg, statusFrete));
        } catch (Exception e) {
            erro.setValue(e);
        }
    }
    private NegociacaoState criarNegociacao(CotacaoState cotacao, BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, StatusFrete statusFrete, BigDecimal comissaoTotal) {
        BigDecimal fretePorKg = distribuirFretePorKg(freteTotalLote, peso, quantidade);
        BigDecimal comissaoPorKg = distribuirComissaoPorKg(comissaoTotal, peso);
        PropostaState proposta = precificarProposta(peso, quantidade, fretePorKg, statusFrete);
        FechamentoState fechamento = precificarFechamento(peso, quantidade, comissaoPorKg);
        return new NegociacaoState(cotacao, proposta, fechamento);
    }
    private NegociacaoState atualizarPropostaPorKg(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        BigDecimal novoValorPorCabeca = converterKgParaCabeca(novoValorPorKg, peso);
        return atualizarPropostaEFechamento(cotacao, fechamento, novoValorPorKg, novoValorPorCabeca, peso, quantidade, fretePorKg, statusFrete);
    }

    private NegociacaoState atualizarPropostaPorCabeca(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        BigDecimal novoValorPorKg = converterCabecaParaKg(novoValorPorCabeca, peso);
        return atualizarPropostaEFechamento(cotacao, fechamento, novoValorPorKg, novoValorPorCabeca, peso, quantidade, fretePorKg, statusFrete);
    }

    private NegociacaoState atualizarPropostaEFechamento(CotacaoState cotacao, FechamentoState fechamento, BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        PropostaState propostaReajustada = novaProposta(valorPorKg, valorPorCabeca, quantidade, fretePorKg, statusFrete);
        FechamentoState fechamentoReajustado = atualizarFechamento(fechamento, valorPorKg, fretePorKg, peso, quantidade);
        return new NegociacaoState(cotacao, propostaReajustada, fechamentoReajustado);
    }

    private PropostaState precificarProposta(BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        PrecificacaoBezerro p = precificar(new PrecificacaoBezerroSemFrete(precificacaoBezerroRepository, fretePorKg), peso, quantidade);
        return novaProposta(p.getValorPorKg(), p.getValorPorCabeca(), quantidade, fretePorKg, statusFrete);
    }

    private FechamentoState precificarFechamento(BigDecimal peso, Integer quantidade, BigDecimal comissaoPorKg) {
        PrecificacaoBezerro p = precificar(new PrecificacaoBezerroComFreteEComissao(precificacaoBezerroRepository, comissaoPorKg), peso, quantidade);
        return novoFechamento(p.getValorPorKg(), p.getValorPorCabeca(), p.getValorTotal(), comissaoPorKg);
    }

    private PrecificacaoBezerro precificar(PrecificacaoBezerroStrategy strategy, BigDecimal peso, Integer quantidade) {
        return new PrecificacaoBezerroImplementation(strategy, valorReferenciaRepository).executar(peso, quantidade);
    }

    private PropostaState novaProposta(BigDecimal valorPorKg, BigDecimal valorPorCabeca, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        return new PropostaState(valorPorKg, valorPorCabeca, calcularTotalLote(valorPorCabeca, quantidade), fretePorKg, statusFrete);
    }

    private FechamentoState novoFechamento(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal comissaoPorKg) {
        return new FechamentoState(valorPorKg, valorPorCabeca, valorTotal, comissaoPorKg);
    }

    private FechamentoState atualizarFechamento(FechamentoState fechamento, BigDecimal valorPorKgProposta, BigDecimal fretePorKg, BigDecimal peso, Integer quantidade) {
        if (fechamento == null || fechamento.getComissaoPorKg() == null) return fechamento;
        BigDecimal novoValorPorKg = valorPorKgProposta.add(fretePorKg).add(fechamento.getComissaoPorKg()).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal novoValorPorCabeca = converterKgParaCabeca(novoValorPorKg, peso);
        return new FechamentoState(novoValorPorKg, novoValorPorCabeca, calcularTotalLote(novoValorPorCabeca, quantidade), fechamento.getComissaoPorKg());
    }

    private BigDecimal distribuirFretePorKg(BigDecimal freteTotalLote, BigDecimal peso, Integer quantidade) {
        BigDecimal pesoTotal = peso.multiply(BigDecimal.valueOf(quantidade));
        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return freteTotalLote.divide(pesoTotal, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal distribuirComissaoPorKg(BigDecimal comissaoTotal, BigDecimal peso) {
        if (peso.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return comissaoTotal.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal converterKgParaCabeca(BigDecimal valorPorKg, BigDecimal peso) {
        return valorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal converterCabecaParaKg(BigDecimal valorPorCabeca, BigDecimal peso) {
        return valorPorCabeca.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularTotalLote(BigDecimal valorPorCabeca, Integer quantidade) {
        return valorPorCabeca.multiply(BigDecimal.valueOf(quantidade))
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private Double extrairVariacao(NegociacaoState s) {
        if (s == null || s.getCotacao() == null || s.getFechamento() == null) return 0.0;
        return calcularVariacao(s.getCotacao(), s.getFechamento());
    }

    private double calcularVariacao(CotacaoState cotacao, FechamentoState fechamento) {
        return fechamento.getValorTotal().subtract(cotacao.getValorTotal())
                .divide(cotacao.getValorTotal(), ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .multiply(CEM).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO)
                .doubleValue();
    }

    public void limpar() {
        state.setValue(new NegociacaoState(null, null, null));
    }

    public void limparParcialmente(CotacaoState cotacao) {
        state.setValue(new NegociacaoState(cotacao, null, null));
    }
}