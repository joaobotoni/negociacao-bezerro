package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.data.repositories.ValorReferenciaRepository;
import com.omni.negociacaobezerros.domain.implementation.PrecificacaoBezerroImplementation;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroComFrete;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class SimulacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<CotacaoState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);

    @Inject
    public SimulacaoViewModel(TaskHelper taskHelper, PrecificacaoBezerroRepository precificacaoBezerroRepository, ValorReferenciaRepository valorReferenciaRepository) {
        this.taskHelper = taskHelper;
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
    }

    public LiveData<CotacaoState> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void processarCotacao(BigDecimal peso, Integer quantidade) {
        tarefas.adicionar(taskHelper.execute(() -> calcularCotacao(peso, quantidade), state::postValue, error::postValue));
    }

    public void limpar() {
        state.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tarefas.cancelarTudo();
    }

    private CotacaoState calcularCotacao(BigDecimal peso, Integer quantidade) {
        PrecificacaoBezerro precificacao = precificarBezerroComFrete(peso, quantidade);
        return new CotacaoState(precificacao.getValorPorKg(), precificacao.getValorPorCabeca(), quantidade, precificacao.getValorTotal());
    }

    private PrecificacaoBezerro precificarBezerroComFrete(BigDecimal peso, Integer quantidade) {
        return new PrecificacaoBezerroImplementation(
                new PrecificacaoBezerroComFrete(precificacaoBezerroRepository),
                valorReferenciaRepository).executar(peso, quantidade);
    }
}
