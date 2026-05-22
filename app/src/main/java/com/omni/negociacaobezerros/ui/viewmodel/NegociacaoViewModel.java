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
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    public final LiveData<PropostaState> proposta = Transformations.map(state, this::extrairProposta);
    public final LiveData<FechamentoState> fechamento = Transformations.map(state, this::extrairFechamento);
    public final LiveData<Double> variacao = Transformations.map(state, this::extrairVariacao);

    @Inject
    public NegociacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository, ValorReferenciaRepository valorReferenciaRepository, TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }


    public LiveData<NegociacaoState> getState() {
        return state;
    }

    public LiveData<Throwable> getErro() {
        return erro;
    }
    public LiveData<PropostaState> getProposta() {
        return proposta;
    }

    public LiveData<FechamentoState> getFechamento() {
        return fechamento;
    }

    public LiveData<Double> getVariacao() {
        return variacao;
    }

    public void processarNegociacao(CotacaoState cotacao, BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, StatusFrete statusFrete, BigDecimal comissaoTotal) {

        BigDecimal fretePorKg = Calculator.distribuirFretePorKg(freteTotalLote, peso, quantidade);
        BigDecimal comissaoPorKg = Calculator.distribuirComissaoPorKg(comissaoTotal, peso);

        if (Validator.isEstadoAtualCompativel(state.getValue(), fretePorKg, statusFrete, comissaoPorKg))
            return;

        taskHelper.execute(() -> Factory.criarNegociacao(precificacaoBezerroRepository, valorReferenciaRepository, cotacao, peso, quantidade, fretePorKg, statusFrete, comissaoPorKg), state::postValue, erro::setValue);
    }

    public void recalcularPropostaPorKg(CotacaoState cotacao, FechamentoState fechamentoAtual, BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        try {
            state.setValue(Factory.atualizarNegociacaoPorKg(cotacao, fechamentoAtual, novoValorPorKg, peso, quantidade, fretePorKg, statusFrete));
        } catch (Exception e) {
            erro.setValue(e);
        }
    }

    public void recalcularPropostaPorCabeca(CotacaoState cotacao, FechamentoState fechamentoAtual, BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
        try {
            state.setValue(Factory.atualizarNegociacaoPorCabeca(cotacao, fechamentoAtual, novoValorPorCabeca, peso, quantidade, fretePorKg, statusFrete));
        } catch (Exception e) {
            erro.setValue(e);
        }
    }

    public void limpar() {
        state.setValue(new NegociacaoState(null, null, null));
    }

    public void limparParcialmente(CotacaoState cotacao) {
        state.setValue(new NegociacaoState(cotacao, null, null));
    }

    private PropostaState extrairProposta(NegociacaoState s) {
        return s != null ? s.getProposta() : null;
    }

    private FechamentoState extrairFechamento(NegociacaoState s) {
        return s != null ? s.getFechamento() : null;
    }

    private Double extrairVariacao(NegociacaoState s) {
        if (!hasEstadoParaVariacao(s)) return 0.0;
        return Calculator.calcularVariacaoPercentual(s.getCotacao().getValorTotal(), s.getFechamento().getValorTotal());
    }

    private boolean hasEstadoParaVariacao(NegociacaoState s) {
        return s != null && s.getCotacao() != null && s.getFechamento() != null;
    }

    private static final class Validator {

        static boolean isEstadoAtualCompativel(NegociacaoState estadoAtual, BigDecimal fretePorKgNovo, StatusFrete statusFreteNovo, BigDecimal comissaoPorKgNova) {
            if (!hasEstadoCompleto(estadoAtual)) return false;
            return isFreteCompativel(estadoAtual.getProposta(), statusFreteNovo, fretePorKgNovo) && isComissaoCompativel(estadoAtual.getFechamento(), comissaoPorKgNova);
        }

        private static boolean hasEstadoCompleto(NegociacaoState estado) {
            return estado != null && estado.getProposta() != null && estado.getFechamento() != null;
        }

        private static boolean isFreteCompativel(PropostaState proposta, StatusFrete statusFrete, BigDecimal fretePorKgNovo) {
            return proposta.getFreteState() == statusFrete && Calculator.isValorIgual(proposta.getFretePorKg(), fretePorKgNovo);
        }

        private static boolean isComissaoCompativel(FechamentoState fechamento, BigDecimal comissaoPorKgNova) {
            return Calculator.isValorIgual(fechamento.getComissaoPorKg(), comissaoPorKgNova);
        }
    }

    private static final class Factory {

        static NegociacaoState criarNegociacao(PrecificacaoBezerroRepository precificacaoRepo, ValorReferenciaRepository referenciaRepo, CotacaoState cotacao, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete, BigDecimal comissaoPorKg) {
            PropostaState proposta = precificarProposta(precificacaoRepo, referenciaRepo, peso, quantidade, fretePorKg, statusFrete);
            FechamentoState fechamento = precificarFechamento(precificacaoRepo, referenciaRepo, peso, quantidade, comissaoPorKg);
            return new NegociacaoState(cotacao, proposta, fechamento);
        }

        static NegociacaoState atualizarNegociacaoPorKg(CotacaoState cotacao, FechamentoState fechamentoAtual, BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
            BigDecimal novoValorPorCabeca = Calculator.converterKgParaCabeca(novoValorPorKg, peso);
            return montarNegociacaoAtualizada(cotacao, fechamentoAtual, novoValorPorKg, novoValorPorCabeca, peso, quantidade, fretePorKg, statusFrete);
        }

        static NegociacaoState atualizarNegociacaoPorCabeca(CotacaoState cotacao, FechamentoState fechamentoAtual, BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
            BigDecimal novoValorPorKg = Calculator.converterCabecaParaKg(novoValorPorCabeca, peso);
            return montarNegociacaoAtualizada(cotacao, fechamentoAtual, novoValorPorKg, novoValorPorCabeca, peso, quantidade, fretePorKg, statusFrete);
        }

        private static NegociacaoState montarNegociacaoAtualizada(CotacaoState cotacao, FechamentoState fechamentoAtual, BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
            PropostaState proposta = montarProposta(valorPorKg, valorPorCabeca, quantidade, fretePorKg, statusFrete);
            FechamentoState fechamento = recalcularFechamento(fechamentoAtual, valorPorKg, fretePorKg, peso, quantidade);
            return new NegociacaoState(cotacao, proposta, fechamento);
        }

        private static PropostaState precificarProposta(PrecificacaoBezerroRepository precificacaoRepo, ValorReferenciaRepository referenciaRepo, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
            PrecificacaoBezerro p = precificar(new PrecificacaoBezerroSemFrete(precificacaoRepo, fretePorKg), referenciaRepo, peso, quantidade);
            return montarProposta(p.getValorPorKg(), p.getValorPorCabeca(), quantidade, fretePorKg, statusFrete);
        }

        private static FechamentoState precificarFechamento(PrecificacaoBezerroRepository precificacaoRepo, ValorReferenciaRepository referenciaRepo, BigDecimal peso, Integer quantidade, BigDecimal comissaoPorKg) {
            PrecificacaoBezerro p = precificar(new PrecificacaoBezerroComFreteEComissao(precificacaoRepo, comissaoPorKg), referenciaRepo, peso, quantidade);
            return montarFechamento(p.getValorPorKg(), p.getValorPorCabeca(), p.getValorTotal(), comissaoPorKg);
        }

        private static PrecificacaoBezerro precificar(PrecificacaoBezerroStrategy strategy, ValorReferenciaRepository referenciaRepo, BigDecimal peso, Integer quantidade) {
            return new PrecificacaoBezerroImplementation(strategy, referenciaRepo).executar(peso, quantidade);
        }

        private static PropostaState montarProposta(BigDecimal valorPorKg, BigDecimal valorPorCabeca, Integer quantidade, BigDecimal fretePorKg, StatusFrete statusFrete) {
            return new PropostaState(valorPorKg, valorPorCabeca, Calculator.calcularTotalLote(valorPorCabeca, quantidade), fretePorKg, statusFrete);
        }

        private static FechamentoState montarFechamento(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal comissaoPorKg) {
            return new FechamentoState(valorPorKg, valorPorCabeca, valorTotal, comissaoPorKg);
        }

        private static FechamentoState recalcularFechamento(FechamentoState fechamento, BigDecimal valorPorKgProposta, BigDecimal fretePorKg, BigDecimal peso, Integer quantidade) {
            if (!hasFechamentoComComissao(fechamento)) return fechamento;
            BigDecimal novoValorPorKg = Calculator.somarComponentesValorKg(valorPorKgProposta, fretePorKg, fechamento.getComissaoPorKg());
            BigDecimal novoValorPorCabeca = Calculator.converterKgParaCabeca(novoValorPorKg, peso);
            BigDecimal novoValorTotal = Calculator.calcularTotalLote(novoValorPorCabeca, quantidade);
            return montarFechamento(novoValorPorKg, novoValorPorCabeca, novoValorTotal, fechamento.getComissaoPorKg());
        }

        private static boolean hasFechamentoComComissao(FechamentoState fechamento) {
            return fechamento != null && fechamento.getComissaoPorKg() != null;
        }
    }

    private static final class Calculator {

        static BigDecimal distribuirFretePorKg(BigDecimal freteTotalLote, BigDecimal peso, Integer quantidade) {
            BigDecimal pesoTotal = peso.multiply(BigDecimal.valueOf(quantidade));
            if (isPesoZero(pesoTotal)) return BigDecimal.ZERO;
            return freteTotalLote.divide(pesoTotal, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        }

        static BigDecimal distribuirComissaoPorKg(BigDecimal comissaoTotal, BigDecimal peso) {
            if (isPesoZero(peso)) return BigDecimal.ZERO;
            return comissaoTotal.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        }

        static BigDecimal converterKgParaCabeca(BigDecimal valorPorKg, BigDecimal peso) {
            return valorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        }

        static BigDecimal converterCabecaParaKg(BigDecimal valorPorCabeca, BigDecimal peso) {
            return valorPorCabeca.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        }

        static BigDecimal somarComponentesValorKg(BigDecimal valorPorKg, BigDecimal fretePorKg, BigDecimal comissaoPorKg) {
            return valorPorKg.add(fretePorKg).add(comissaoPorKg).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        }

        static BigDecimal calcularTotalLote(BigDecimal valorPorCabeca, Integer quantidade) {
            return valorPorCabeca.multiply(BigDecimal.valueOf(quantidade)).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        }

        static double calcularVariacaoPercentual(BigDecimal valorReferencia, BigDecimal valorComparado) {
            return valorComparado.subtract(valorReferencia).divide(valorReferencia, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO).multiply(CEM).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO).doubleValue();
        }

        static boolean isValorIgual(BigDecimal a, BigDecimal b) {
            if (a == null) return b == null;
            return b != null && a.compareTo(b) == 0;
        }

        private static boolean isPesoZero(BigDecimal peso) {
            return peso.compareTo(BigDecimal.ZERO) == 0;
        }
    }
}