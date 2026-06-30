package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.ParametrosBezerro;
import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.ValorReferenciaRepository;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroComFrete;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.CotacaoUiState;
import com.omni.negociacaobezerros.utils.format.Numbers;

import java.math.BigDecimal;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CotacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;

    private final MutableLiveData<CotacaoUiState> cotacaoState = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> peso = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> quantidade = new MutableLiveData<>(null);
    private final MutableLiveData<String> sexo = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public CotacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository,
                             ValorReferenciaRepository valorReferenciaRepository,
                             TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<CotacaoUiState> getCotacaoState() { return cotacaoState; }
    public LiveData<BigDecimal> getPeso() { return peso; }
    public LiveData<Integer> getQuantidade() { return quantidade; }
    public LiveData<String> getSexo() { return sexo; }
    public LiveData<Throwable> getErro() { return erro; }

    public void calcular(BigDecimal pesoInformado, Integer quantidadeInformada) {
        peso.setValue(pesoInformado);
        quantidade.setValue(quantidadeInformada);
        taskHelper.execute(() -> calcularPrecificacao(pesoInformado, quantidadeInformada),
                this::postCotacaoState, erro::setValue);
    }

    public void setSexo(String sexoSelecionado) {
        sexo.setValue(sexoSelecionado);
    }

    public void limpar() {
        cotacaoState.setValue(null);
        peso.setValue(null);
        quantidade.setValue(null);
        sexo.setValue(null);
        erro.setValue(null);
    }

    private PrecificacaoBezerro calcularPrecificacao(BigDecimal pesoInformado, Integer quantidadeInformada) {
        ParametrosBezerro parametros = carregarParametros();
        return new PrecificacaoBezerroComFrete(precificacaoBezerroRepository)
                .calcular(pesoInformado, quantidadeInformada, parametros);
    }

    private ParametrosBezerro carregarParametros() {
        ValorReferencia referencia = valorReferenciaRepository.findMaisRecente()
                .orElseThrow(() -> new IllegalStateException("Nenhum valor de referência cadastrado"));
        return new ParametrosBezerro(
                Numbers.parseDecimal(referencia.getPesoBezerro()),
                Numbers.parseDecimal(referencia.getValorArrobaBoi()),
                Numbers.parseDecimal(referencia.getAgioBezerro()));
    }

    private void postCotacaoState(PrecificacaoBezerro precificacao) {
        cotacaoState.setValue(new CotacaoUiState(
                precificacao.getValorPorKg(),
                precificacao.getValorPorCabeca(),
                precificacao.getValorTotal()));
    }
}
